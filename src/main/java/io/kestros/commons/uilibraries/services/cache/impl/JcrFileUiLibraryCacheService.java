/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.kestros.commons.uilibraries.services.cache.impl;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsClosestType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.JcrFileCacheService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.MatchingResourceTypeNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing, building, retrieving and purging UiLibrary output caches.  Saves output as
 * nt:file Resources under /var/cache/ui-libraries.
 */
@Component(immediate = true,
           service = {ManagedCacheService.class, UiLibraryCacheService.class},
           property = "service.ranking:Integer=100")
public class JcrFileUiLibraryCacheService extends JcrFileCacheService
    implements UiLibraryCacheService {

  private static final Logger LOG = LoggerFactory.getLogger(JcrFileUiLibraryCacheService.class);

  private static final String UI_LIBRARY_CACHE_PURGE_SERVICE_USER = "ui-library-cache-service";
  private static final long serialVersionUID = 8442978263338882415L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ResourceResolverFactory resourceResolverFactory;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ModelFactory modelFactory;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient JobManager jobManager;

  @Override
  public JobManager getJobManager() {
    return jobManager;
  }

  @Override
  public String getDisplayName() {
    return "Ui Library Cache Service";
  }

  @Override
  protected void afterCachePurgeComplete(ResourceResolver resourceResolver) {
    // does nothing
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
  public String getCachedOutput(@Nonnull final UiLibrary uiLibrary,
      @Nonnull final ScriptType scriptType, final boolean minified) throws CacheRetrievalException {

    try {
      return getCachedFile(uiLibrary.getPath() + getScriptFileSuffix(scriptType, minified),
          scriptType.getFileModelClass()).getFileContent();
    } catch (final IOException | ResourceNotFoundException
                               | InvalidResourceTypeException exception) {
      throw new CacheRetrievalException(exception.getMessage());
    }
  }

  @Override
  public void cacheUiLibraryScripts(@Nonnull final UiLibrary uiLibrary,
      final boolean cacheMinified) {
    LOG.info("Caching CSS/JS scripts for {}.", uiLibrary.getPath());
    for (ScriptType supportedScriptType : uiLibrary.getSupportedScriptTypes()) {
      if (supportedScriptType == ScriptType.LESS) {
        supportedScriptType = ScriptType.CSS;
      }
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

  @SuppressWarnings("unused")
  @Override
  public void cacheUiLibraryScripts(final String uiLibraryPath, final boolean cacheMinified)
      throws CacheBuilderException {
    if (getServiceResourceResolver() != null) {
      try {
        final BaseResource uiLibraryResource = getResourceAsBaseResource(uiLibraryPath,
            getServiceResourceResolver());
        final BaseResource uiLibrary = getResourceAsClosestType(uiLibraryResource.getResource(),
            modelFactory);
        if (uiLibrary instanceof UiLibrary) {
          cacheUiLibraryScripts((UiLibrary) uiLibrary, cacheMinified);
        }

      } catch (final ResourceNotFoundException | MatchingResourceTypeNotFoundException e) {
        throw new CacheBuilderException(e.getMessage());
      }
    } else {
      throw new CacheBuilderException(String.format(
          "Failed to build cache for UiLibrary %s. Null or closed service ResourceResolver.",
          uiLibraryPath));
    }

  }

  private void cacheOutput(final UiLibrary uiLibrary, final ScriptType scriptType,
      final boolean minify) throws CacheBuilderException {
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

  @Override
  protected List<String> getRequiredResourcePaths() {
    List<String> requiredPaths = new ArrayList<>();
    requiredPaths.add(getServiceCacheRootPath());
    return requiredPaths;
  }
}
