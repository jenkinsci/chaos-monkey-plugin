package io.jenkins.plugins.chaosmonkey;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.jvnet.hudson.test.RestartableJenkinsRule;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pierre Beitz
 * on 2019-08-04.
 */
public class ChaosMonkeyConfigurationTest {

  @Rule
  public RestartableJenkinsRule rr = new RestartableJenkinsRule();

  /**
   * Tries to exercise enough code paths to catch common mistakes:
   * <ul>
   * <li>missing {@code load}
   * <li>missing {@code save}
   * <li>misnamed or absent getter/setter
   * <li>misnamed {@code textbox}
   * </ul>
   */
  @Test
  public void uiAndStorage() {
    String path = "configure/.*";
    int latency = 5000;
    double ratio = 0.3;
    rr.then(r -> {
      HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
      List<HtmlElement> elementsByName = findElementsByName(config.getElementsByAttribute("button", "type", "button"), "Add latency item");
      assertEquals(elementsByName.size(), 1);
      HtmlElement addButton = elementsByName.get(0);
      addButton.click();
      HtmlTextInput textbox = config.getInputByName("_.path");
      textbox.setText(path);
      textbox = config.getInputByName("_.latency");
      textbox.setText(Integer.toString(latency));
      textbox = config.getInputByName("_.ratio");
      textbox.setText(Double.toString(ratio));
      r.submit(config);
      assertEquals("global config page let us edit the path", path, ChaosMonkeyConfiguration.get().getLatencies().get(0).getPath());
      assertEquals("global config page let us edit the latency", latency, ChaosMonkeyConfiguration.get().getLatencies().get(0).getLatency());
      assertEquals("global config page let us edit the ratio", ratio, ChaosMonkeyConfiguration.get().getLatencies().get(0).getRatio(), 0d);
    });
    rr.then(r -> {
      assertEquals("global config page let us edit it", path, ChaosMonkeyConfiguration.get().getLatencies().get(0).getPath());
      assertEquals("global config page let us edit it", latency, ChaosMonkeyConfiguration.get().getLatencies().get(0).getLatency());
      assertEquals("global config page let us edit it", ratio, ChaosMonkeyConfiguration.get().getLatencies().get(0).getRatio(), 0d);
    });
  }

  private List<HtmlElement> findElementsByName(List<HtmlElement> elements, @Nonnull String name) {
    return elements.stream().filter(el -> name.equals(el.getTextContent())).collect(Collectors.toList());
  }

}
