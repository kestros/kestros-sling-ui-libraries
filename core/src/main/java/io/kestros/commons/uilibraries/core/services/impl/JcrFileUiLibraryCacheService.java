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

package io.kestros.commons.uilibraries.core.services.impl;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.JcrFileCacheService;
import io.kestros.commons.structuredslingmodels.exceptions.JcrFileReadException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing, building, retrieving and purging UiLibrary output caches.  Saves output as
 * nt:file Resources under /var/cache/ui-libraries.
 */
@SuppressFBWarnings({"FCBL_FIELD_COULD_BE_LOCAL","IMC_IMMATURE_CLASS_NO_TOSTRING"})
@Component(immediate = true, service = {ManagedCacheService.class, UiLibraryCacheService.class},
        property = "service.ranking:Integer=100")
public class JcrFileUiLibraryCacheService extends JcrFileCacheService implements
        UiLibraryCacheService {

  private static final Logger LOG = LoggerFactory.getLogger(JcrFileUiLibraryCacheService.class);

  private static final String UI_LIBRARY_CACHE_PURGE_SERVICE_USER = "ui-library-manager";
  private static final long serialVersionUID = 8442978263338882415L;

  @SuppressFBWarnings({"SE_TRANSIENT_FIELD_NOT_RESTORED", "SE_BAD_FIELD"})
  @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption =
          ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  @SuppressFBWarnings({"SE_TRANSIENT_FIELD_NOT_RESTORED", "SE_BAD_FIELD"})
  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @SuppressFBWarnings({"SE_TRANSIENT_FIELD_NOT_RESTORED", "SE_BAD_FIELD"})
  @Reference
  private ModelFactory modelFactory;

  @SuppressFBWarnings({"SE_TRANSIENT_FIELD_NOT_RESTORED", "SE_BAD_FIELD"})
  @Reference
  private JobManager jobManager;

  @Nullable
  @SuppressFBWarnings("MALICIOUS_CODE")
  @Override
  public JobManager getJobManager() {
    return jobManager;
  }

  @Nonnull
  @Override
  public String getDisplayName() {
    return "Ui Library Cache Service";
  }

  @Override
  protected void afterCachePurgeComplete(@Nonnull ResourceResolver resourceResolver) {
    // does nothing
  }

  @Override
  protected long getMinimumTimeBetweenCachePurges() {
    return 3000;
  }

  @Nonnull
  @Override
  public String getCacheCreationJobName() {
    return "kestros/ui-libraries/cache";
  }

  @Nonnull
  @Override
  public String getServiceCacheRootPath() {
    return "/var/cache/ui-libraries";
  }

  @Nonnull
  @Override
  protected String getServiceUserName() {
    return UI_LIBRARY_CACHE_PURGE_SERVICE_USER;
  }

  @Nonnull
  @Override
  protected Logger getLogger() {
    return LOG;
  }

  @Nullable
  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Nonnull
  @Override
  protected List<String> getRequiredResourcePaths() {
    return Collections.singletonList(getServiceCacheRootPath());
  }

  @Nonnull
  @Override
  public String getCachedOutput(@Nonnull String libraryPath, ScriptType scriptType,
          boolean minified, @Nonnull ResourceResolver resourceResolver) throws
          CacheRetrievalException {
    String cachedResourcePath = String.format("%s%s%s", getServiceCacheRootPath(), libraryPath,
            scriptType.getExtension());
    if (minified) {
      cachedResourcePath = String.format("%s%s.min%s", getServiceCacheRootPath(), libraryPath,
              scriptType.getExtension());
    }
    try {
      BaseFile file = getResourceAsType(cachedResourcePath, resourceResolver,
              scriptType.getFileModelClass());
      return file.getFileContent();
    } catch (ModelAdaptionException | IOException | JcrFileReadException e) {
      throw new CacheRetrievalException(e.getMessage(), e);
    }
  }

  @Nonnull
  @Override
  public String getCachedOutput(FrontendLibrary library, @Nonnull ScriptType scriptType,
          boolean minified, @Nonnull ResourceResolver resourceResolver) throws
          CacheRetrievalException {
    return getCachedOutput(library.getPath(), scriptType, minified, resourceResolver);
  }

  @Override
  public void cacheUiLibraryScript(@Nonnull String libraryPath, @Nonnull String content,
          @Nonnull ScriptType scriptType, boolean isMinified,
          ResourceResolver resourceResolver) throws CacheBuilderException {
    if (isMinified) {
      LOG.info("Attempting to cache minified script for library {}",
              libraryPath.replaceAll("[\r\n]", ""));
      createCacheFile(content, String.format("%s.min%s", libraryPath, scriptType.getExtension()),
              scriptType, resourceResolver);
    } else {
      LOG.info("Attempting to cache non-minified script for library {}",
              libraryPath.replaceAll("[\r\n]", ""));
      createCacheFile(content, String.format("%s%s", libraryPath, scriptType.getExtension()),
              scriptType, resourceResolver);
    }
  }
}