package io.kestros.commons.uilibraries.services.cache;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsClosestType;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.JcrFileCacheService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.MatchingResourceTypeNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true,
           service = {ManagedCacheService.class, UiLibraryCacheService.class},
           property = "service.ranking:Integer=100")
public class BaseUiLibraryCacheService extends JcrFileCacheService
    implements UiLibraryCacheService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseUiLibraryCacheService.class);

  private static final String UI_LIBRARY_CACHE_PURGE_SERVICE_USER = "ui-library-cache-service";

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Reference
  private ModelFactory modelFactory;

  @Reference
  private JobManager jobManager;

  @Override
  public JobManager getJobManager() {
    return jobManager;
  }

  @Override
  public String getDisplayName() {
    return "Ui Library Cache Service";
  }

  @Override
  protected long getMinimumTimeBetweenCachePurges() {
    return 3000;
  }

  @Override
  public String getCacheCreationJobName() {
    return "kestros/ui-libraries/cache";
  }

  @Override
  public String getServiceCacheRootPath() {
    return "/var/cache/ui-libraries";
  }

  @Override
  protected String getServiceUserName() {
    return UI_LIBRARY_CACHE_PURGE_SERVICE_USER;
  }

  @Override
  public String getCachedOutput(@Nonnull final UiLibrary uiLibrary, @Nonnull final ScriptType scriptType,
      final boolean minified) throws CacheRetrievalException {

    try {
      return getCachedFile(uiLibrary.getPath() + getScriptFileSuffix(scriptType, minified),
          scriptType.getFileModelClass()).getOutput();
    } catch (final IOException | ResourceNotFoundException | InvalidResourceTypeException exception) {
      throw new CacheRetrievalException(exception.getMessage());
    }

  }

  @Override
  public void cacheUiLibraryScripts(@Nonnull final UiLibrary uiLibrary, final boolean cacheMinified) {
    LOG.info("Caching CSS/JS scripts for {}.", uiLibrary.getPath());
    for (final ScriptType supportedScriptType : uiLibrary.getSupportedScriptTypes()) {
      try {
        cacheOutput(uiLibrary, supportedScriptType, false);
      } catch (final CacheBuilderException exception) {
        LOG.error("Failed to cache non-minified scripts for {}. {}", uiLibrary.getPath(),
            exception.getMessage());
      }
      if (cacheMinified) {
        try {
          cacheOutput(uiLibrary, supportedScriptType, true);
        } catch (final CacheBuilderException exception) {
          LOG.error("Failed to cache minified scripts for {}. {}", uiLibrary.getPath(),
              exception.getMessage());
        }
      }
    }
  }


  @Override
  public void cacheUiLibraryScripts(final String uiLibraryPath, final boolean cacheMinified)
      throws CacheBuilderException {
    try {
      final BaseResource uiLibraryResource = getResourceAsBaseResource(uiLibraryPath,
          getServiceResourceResolver());
      final BaseResource uiLibrary = getResourceAsClosestType(uiLibraryResource.getResource(),
          modelFactory);
      if (uiLibrary instanceof UiLibrary) {
        cacheUiLibraryScripts((UiLibrary) uiLibrary, cacheMinified);
      }

    } catch (final ResourceNotFoundException e) {
      throw new CacheBuilderException(e.getMessage());
    } catch (final MatchingResourceTypeNotFoundException e) {
      throw new CacheBuilderException(e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getCacheCreationJobProperties(final UiLibrary uiLibrary,
      final Boolean cacheMinified) {
    final Map<String, Object> jobProperties = new HashMap<>();
    jobProperties.put("ui-library-path", uiLibrary.getPath());
    jobProperties.put("cache-minified", cacheMinified);
    return jobProperties;
  }

  private void cacheOutput(final UiLibrary uiLibrary, final ScriptType scriptType, final boolean minify)
      throws CacheBuilderException {
    final String fileName = uiLibrary.getPath() + getScriptFileSuffix(scriptType, minify);
    try {
      createCacheFile(uiLibrary.getOutput(scriptType, minify), fileName, scriptType);
    } catch (final InvalidResourceTypeException exception) {
      throw new CacheBuilderException(exception.getMessage());
    }
  }

  static String getScriptFileSuffix(final ScriptType scriptType, final boolean minify) {
    String fileName = scriptType.getExtension();
    if (minify) {
      fileName = ".min" + scriptType.getExtension();
    }
    return fileName;
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }
}
