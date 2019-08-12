package io.jenkins.plugins.issueinject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import hudson.Extension;
import hudson.init.Initializer;
import hudson.util.PluginServletFilter;
import jenkins.model.Jenkins;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hudson.init.InitMilestone.PLUGINS_PREPARED;

/**
 * Created by Pierre Beitz
 * on 2019-08-04.
 */
@Extension
public class LatencyFilter implements Filter {
  private static final Logger LOG = Logger.getLogger(LatencyFilter.class.getName());

  @Inject
  private IssueInjectConfiguration configuration;

  @Initializer(after = PLUGINS_PREPARED)
  public static void init() throws ServletException {
    Injector injector = Jenkins.get().getInjector();
    if (injector == null) {
      return;
    }
    PluginServletFilter.addFilter(injector.getInstance(LatencyFilter.class));
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    if (!(request instanceof HttpServletRequest)) {
      LOG.log(Level.WARNING, "expecting an HttpServletRequest, got: {0}", request.getClass().getName());
      chain.doFilter(request, response);
      return;
    }

    List<LatencyRule> latencies = configuration.getLatencies();
    if (latencies != null) {
      latencies.forEach(latencyRule -> latencyRule.apply((HttpServletRequest) request));
    }
    chain.doFilter(request, response);
  }


  @Override
  public void destroy() {

  }
}
