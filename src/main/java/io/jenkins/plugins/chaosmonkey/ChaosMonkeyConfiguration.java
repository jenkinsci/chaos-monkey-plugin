package io.jenkins.plugins.chaosmonkey;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.List;

/**
 * Created by Pierre Beitz
 * on 2019-08-04.
 */
@Symbol("chaos-monkey")
@Extension
public class ChaosMonkeyConfiguration extends GlobalConfiguration {

  /**
   * @return the singleton instance
   */
  public static ChaosMonkeyConfiguration get() {
    return GlobalConfiguration.all().get(ChaosMonkeyConfiguration.class);
  }

  private List<LatencyRule> latencies;

  public ChaosMonkeyConfiguration() {
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

}
