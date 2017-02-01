package com.google.cloud.tools.eclipse.appengine.deploy;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A utility class for App Engine deploy.
 */
// TODO: move into appengine-core-plugin
public class AppEngineDeployUtil {
  /**
   * Parse the raw json output of the deployment.
   *
   * @return an object modeling the output of a deploy command
   * @throws JsonParseException if unable to extract the deploy output information needed
   */
  public static DeployOutput parseDeployOutput(String jsonOutput) throws JsonParseException {
    Type deployOutputType = new TypeToken<DeployOutput>() {}.getType();
    DeployOutput deployOutput = new Gson().fromJson(jsonOutput, deployOutputType);
    if (deployOutput == null
        || deployOutput.versions == null || deployOutput.versions.size() != 1) {
      throw new JsonParseException("Cannot get app version: unexpected gcloud JSON output format");
    }
    return deployOutput;
  }
  
  /**
   * Holds de-serialized JSON output of gcloud app deploy. Don't change the field names
   * because Gson uses it for automatic de-serialization.
   */
  // TODO expand to include other Version attributes
  public static class DeployOutput {
    private static class Version {
      String id;
      String service;
    }

    List<Version> versions;

    /**
     * @return version, an be null
     */
    public String getVersion() {
      if (versions == null || versions.size() != 1) {
        return null;
      }
      return versions.get(0).id;
    }

    /**
     * @return service, an be null
     */
    public String getService() {
      if (versions == null || versions.size() != 1) {
        return null;
      }
      return versions.get(0).service;
    }
  }

}
