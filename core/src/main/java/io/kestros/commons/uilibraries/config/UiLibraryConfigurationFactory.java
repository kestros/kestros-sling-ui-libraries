package io.kestros.commons.uilibraries.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UI Library Configuration Factory.
 */
@Designate(ocd = UiLibraryConfigurationFactory.Config.class,
           factory = true)
@Component(immediate = true,
           service = UiLibraryConfigurationFactory.class,
           configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class UiLibraryConfigurationFactory implements Serializable {

  private static final long serialVersionUID = -2062013416937224382L;

  @ObjectClassDefinition(name = "Kestros Commons UiLibrary Configuration Service Factory",
                         description = "Allows the configuration of UILibraries")
  public @interface Config {

    /**
     * UILibrary paths that should be minified.
     *
     * @return UILibrary paths that should be minified.
     */
    @AttributeDefinition(name = "Minified UI Library Paths",
                         description = "Path to UI Libraries to be "
                                       + "automatically minified. Requests without the `.min` "
                                       + "selector will be minified,")
    String[] minifiedLibraryPaths() default {};
  }

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryConfigurationFactory.class);

  // TODO get away from using this if possible
  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  private transient Config config;

  /**
   * Activates the ConfigFactory.
   *
   * @param config Current configuration.
   * @param properties Config properties.
   */
  @Activate
  public void activate(final Config config, final Map<String, Object> properties) {
    this.config = config;
    LOG.info("Activating UiLibraryConfiguration. {} UiLibraries to be minified.",
        getMinifiedLibraryPaths().size());
  }

  /**
   * List of UILibrary paths that should be minified.
   *
   * @return List of UILibrary paths that should be minified.
   */
  public List<String> getMinifiedLibraryPaths() {
    return Arrays.asList(config.minifiedLibraryPaths());
  }
}