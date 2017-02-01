package com.google.cloud.tools.eclipse.appengine.deploy;

import com.google.gson.JsonParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link AppEngineDeployUtil}
 */
public class AppEngineDeployUtilTest {
  @Test
  public void testDeployOutputJsonParsingOneVersion() {
    String jsonOutput =
        "{\n" +
            "  \"configs\": [],\n" +
            "  \"versions\": [\n" +
            "    {\n" +
            "      \"id\": \"20160429t112518\",\n" +
            "      \"last_deployed_time\": null,\n" +
            "      \"project\": \"some-project\",\n" +
            "      \"service\": \"default\",\n" +
            "      \"traffic_split\": null,\n" +
            "      \"version\": null\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    AppEngineDeployUtil.DeployOutput deployOutput =
        AppEngineDeployUtil.parseDeployOutput(jsonOutput);
    Assert.assertEquals(deployOutput.getVersion(), "20160429t112518");
    Assert.assertEquals(deployOutput.getService(), "default");
  }

  @Test
  public void testDeployOutputJsonParsingTwoVersions() {
    String jsonOutput =
        "{\n" +
            "  \"configs\": [],\n" +
            "  \"versions\": [\n" +
            "    {\n" +
            "      \"id\": \"20160429t112518\",\n" +
            "      \"last_deployed_time\": null,\n" +
            "      \"project\": \"some-project\",\n" +
            "      \"service\": \"default\",\n" +
            "      \"traffic_split\": null,\n" +
            "      \"version\": null\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"20160429t112518\",\n" +
            "      \"last_deployed_time\": null,\n" +
            "      \"project\": \"some-project\",\n" +
            "      \"service\": \"default\",\n" +
            "      \"traffic_split\": null,\n" +
            "      \"version\": null\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    try {
      AppEngineDeployUtil.parseDeployOutput(jsonOutput);
      Assert.fail();
    } catch (JsonParseException e) {
      // Success! Should throw a JsonParseException.
    }
  }

  @Test
  public void testDeployOutputJsonParsingOldFormat() {
    String jsonOutput =
        "{\n" +
            "  \"default\": \"https://springboot-maven-project.appspot.com\"\n" +
            "}\n";

    try {
      AppEngineDeployUtil.parseDeployOutput(jsonOutput);
      Assert.fail();
    } catch (JsonParseException e) {
      // Success! Should throw a JsonParseException.
    }
  }
}
