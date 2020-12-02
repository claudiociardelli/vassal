package VASSAL.tools.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Reads {@link java.util.Properties} generated by Git-Commit-Id-Plugin
 */
public class GitProperties {

  private static final Logger log = LoggerFactory.getLogger(GitProperties.class);

  private static final String DEFAULT_FILENAME = "git.properties"; //NON-NLS

  private static final String KEY_GIT_VERSION = "git.version"; //NON-NLS

  private static final String DEFAULT_GIT_VERSION = "3.x development version"; //NON-NLS

  private final String filename;
  private final Properties gitProperties;

  public GitProperties() {
    this(DEFAULT_FILENAME);
  }

  public GitProperties(final String filename) {
    this.filename = filename;
    gitProperties = new Properties();
    readGitProperties();
  }

  private void readGitProperties() {
    final ClassLoader classLoader = getClass().getClassLoader();
    try (InputStream is = classLoader.getResourceAsStream(filename)) {
      if (is == null) {
        return;
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
        gitProperties.load(reader);
      }

    }
    catch (IOException e) {
      log.error("Error while trying to read properties from {}", filename, e); //NON-NLS
    }
  }

  public String getVersion() {
    return gitProperties.getProperty(KEY_GIT_VERSION, DEFAULT_GIT_VERSION);
  }

}
