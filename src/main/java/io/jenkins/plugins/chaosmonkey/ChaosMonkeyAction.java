package io.jenkins.plugins.chaosmonkey;

import hudson.Extension;
import hudson.model.Queue;
import hudson.model.RootAction;
import jenkins.model.Jenkins;
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
    Event event = new Event(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), duration);
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

  public List<Event> getEvents() {
    return Collections.unmodifiableList(events);
  }

  private static class Event {
    private final String startTime;
    private final int duration;
    private boolean done;

    private Event(String startTime, int duration) {
      this.startTime = startTime;
      this.duration = duration;
    }

    private void setDone() {
      this.done = true;
    }
  }
}
