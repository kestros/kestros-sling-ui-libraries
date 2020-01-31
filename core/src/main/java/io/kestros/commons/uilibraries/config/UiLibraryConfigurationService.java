package io.kestros.commons.uilibraries.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true,
           service = UiLibraryConfigurationService.class)
public class UiLibraryConfigurationService implements Serializable {

  private static final long serialVersionUID = -5412418185436722061L;

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryConfigurationService.class);

  @Reference(service = UiLibraryConfigurationFactory.class,
             cardinality = ReferenceCardinality.OPTIONAL,
             policy = ReferencePolicy.DYNAMIC,
             bind = "bindUiLibraryConfigurationServiceFactory",
             unbind = "unbindUiLibraryConfigurationServiceFactory")
  private volatile List<UiLibraryConfigurationFactory> configurationList;

  protected void bindUiLibraryConfigurationServiceFactory(
      final UiLibraryConfigurationFactory config) {
    LOG.info("Binding new UiLibraryConfiguration to UiLibraryConfigurationService");
    if (configurationList == null) {
      configurationList = new ArrayList<>();
    }
    configurationList.add(config);
  }

  protected synchronized void unbindUiLibraryConfigurationServiceFactory(
      final UiLibraryConfigurationFactory config) {
    LOG.info("Unbinding UiLibraryConfiguration from UiLibraryConfigurationService");
    configurationList.remove(config);
  }

  /**
   * List of UILibrary paths that should be minified. Compiled from all UiLibraryConfigurations
   *
   * @return List of UILibrary paths that should be minified. Compiled from all
   *     UiLibraryConfigurations
   */
  @Nonnull
  public List<String> getMinifiedLibraryPaths() {
    List<String> minifiedPaths = new ArrayList<>();
    if (configurationList != null) {
      for (Object config : configurationList) {
        UiLibraryConfigurationFactory factoryConfig = (UiLibraryConfigurationFactory) config;
        minifiedPaths.addAll(factoryConfig.getMinifiedLibraryPaths());
      }
      return minifiedPaths;
    }
    LOG.warn("Unable to determine if there are any UiLibraryConfigurations.  No scripts will"
             + "be minified.");
    return Collections.emptyList();
  }
}
