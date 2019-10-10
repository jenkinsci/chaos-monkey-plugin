package io.jenkins.plugins.chaosmonkey;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Queue;
import hudson.model.RootAction;
import jenkins.model.Jenkins;
import org.apache.commons.io.output.NullOutputStream;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.annotation.CheckForNull;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Pierre Beitz
 * on 20/09/2019.
 */
@Extension
public class ChaosMonkeyAction implements RootAction {
  private static final List<Event> events = new ArrayList<>();

  @CheckForNull
  @Override
  public String getIconFileName() {
    return "/plugin/chaos-monkey/images/fire.svg";
  }

  @CheckForNull
  @Override
  public String getDisplayName() {
    return "Chaos Monkey";
  }

  @CheckForNull
  @Override
  public String getUrlName() {
    return "chaos";
  }

  @RequirePOST
  public void doLockTheQueue(@QueryParameter int duration, StaplerRequest request, StaplerResponse response) throws ServletException, IOException {
    Jenkins.get().checkPermission(Jenkins.ADMINISTER);
    Event event = new Event(Event.Type.LOAD, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), duration);
    events.add(event);
    // fire and forget atm, no way to cancel what we did.
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(Queue.wrapWithLock(() -> {
      // if somebody else already triggered this, it will wait for the previous execution to be finished before running this one

      try {
        Thread.sleep(duration);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        event.setDone();
      }
    }));
    response.forwardToPreviousPage(request);
  }

  @RequirePOST
  public void doGenerateLoad(@QueryParameter int duration, StaplerRequest request, StaplerResponse response) throws ServletException, IOException {
    int threadNumber = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
    Event event = new Event(Event.Type.LOCK, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), duration);
    events.add(event);
    CyclicBarrier barrier = new CyclicBarrier(threadNumber, event::setDone);
    for (int i = 0; i < threadNumber; i++) {
      executorService.submit(() -> NullOutputStream.NULL_OUTPUT_STREAM.write(generateLoad(duration, barrier)));
    }
    response.forwardToPreviousPage(request);
  }

  private static int generateLoad(int duration, CyclicBarrier barrier) {
    long startTime = System.currentTimeMillis();
    int count = 0;
    while (System.currentTimeMillis() - startTime < duration) {
      count++;
    }
    try {
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      // silently ignore
    }
    return count;
  }

  public List<Event> getEvents() {
    return Collections.unmodifiableList(events);
  }

  private static class Event {
    private final String startTime;
    private final int duration;
    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "Read in jelly")
    private boolean done;
    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "Read in jelly")
    private Type type;

    private Event(Type type, String startTime, int duration) {
      this.type = type;
      this.startTime = startTime;
      this.duration = duration;
    }

    private void setDone() {
      this.done = true;
    }

    private enum Type {
      LOCK, LOAD
    }
  }
}
