package io.jenkins.plugins.issueinject;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.YesNoMaybe;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Created by Pierre Beitz
 * on 2019-08-04.
 */
public class LatencyRule extends AbstractDescribableImpl<LatencyRule> {
  private String path;
  private Pattern pattern;
  private int latency;
  private double ratio;

  @DataBoundConstructor
  public LatencyRule(String path, int latency, double ratio) {
    this.path = path;
    this.latency = latency;
    this.ratio = ratio;
    pattern = Pattern.compile(path);
  }

  public String getPath() {
    return path;
  }

  public int getLatency() {
    return latency;
  }

  public double getRatio() {
    return ratio;
  }

  public void apply(HttpServletRequest request) {
    if (Math.random() < ratio) {
      if (pattern.matcher(request.getPathInfo()).matches()) {
        try {
          Thread.sleep(latency);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // the filter will be injected at startup only so there is no point to have something else than NO
  @Extension(dynamicLoadable = YesNoMaybe.NO)
  public static final class DescriptorImpl extends Descriptor<LatencyRule> {

    @Nonnull
    @Override
    public String getDisplayName() {
      return "Latency Item";
    }

    public FormValidation doCheckLatency(@QueryParameter String value) {
      if (StringUtils.isEmpty(value)) {
        return FormValidation.warning("Please specify a latency.");
      }
      return FormValidation.ok();
    }

    public FormValidation doCheckPath(@QueryParameter String path) {
      if (StringUtils.isEmpty(path)) {
        return FormValidation.warning("Please specify a path.");
      }
      return FormValidation.ok();
    }

    public FormValidation doCheckRatio(@QueryParameter String ratio) {
      if (StringUtils.isEmpty(ratio)) {
        return FormValidation.warning("Please specify a ratio.");
      }
      return FormValidation.ok();
    }
  }
}
