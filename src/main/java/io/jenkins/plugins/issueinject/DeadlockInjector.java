package io.jenkins.plugins.issueinject;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Queue;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.annotation.Nonnull;

/**
 * Created by Pierre Beitz
 * on 2019-08-07.
 */
public class DeadlockInjector extends AbstractDescribableImpl<DeadlockInjector> {
 private int duration;

  @DataBoundConstructor
  public DeadlockInjector(int duration) {
    this.duration = duration;
  }

  public int getDuration() {
    return duration;
  }

  @Extension
  public static final class DescriptorImpl extends Descriptor<DeadlockInjector> {

    @Nonnull
    @Override
    public String getDisplayName() {
      return "DeadLock Injection";
    }

    @RequirePOST
    public FormValidation doLockTheQueue(@QueryParameter int duration) {
      Jenkins.get().checkPermission(Jenkins.ADMINISTER);
      Queue.withLock(() -> {
        try {
          Thread.sleep(duration);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
      return FormValidation.ok();
    }
  }
}
