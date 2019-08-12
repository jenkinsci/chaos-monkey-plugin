package io.jenkins.plugins.issueinject;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.List;

/**
 * Created by Pierre Beitz
 * on 2019-08-04.
 */
@Symbol("issue-inject")
@Extension
public class IssueInjectConfiguration extends GlobalConfiguration {

  /**
   * @return the singleton instance
   */
  public static IssueInjectConfiguration get() {
    return GlobalConfiguration.all().get(IssueInjectConfiguration.class);
  }

  private List<LatencyRule> latencies;
  private DeadlockInjector deadlockInjector;

  public IssueInjectConfiguration() {
    load();
  }

  /**
   * @return the currently configured label, if any
   */
  public List<LatencyRule> getLatencies() {
    return latencies;
  }

  /**
   * Together with {@link #getLatencies}, binds to entry in {@code config.jelly}.
   *
   * @param latencies the new value of this field
   */
  @DataBoundSetter
  public void setLatencies(List<LatencyRule> latencies) {
    this.latencies = latencies;
    save();
  }

  public DeadlockInjector getDeadlockInjector() {
    return deadlockInjector;
  }

  @DataBoundSetter
  public void setDeadlockInjector(DeadlockInjector deadlockInjector) {
    this.deadlockInjector = deadlockInjector;
    save();
  }

}
