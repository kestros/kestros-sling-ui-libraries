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
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
@Component(immediate = true,
           service = {ManagedCacheService.class, UiLibraryCacheService.class},
           property = "service.ranking:Integer=100")
public class JcrFileUiLibraryCacheService extends JcrFileCacheService
    implements UiLibraryCacheService {

  private static final Logger LOG = LoggerFactory.getLogger(JcrFileUiLibraryCacheService.class);

  private static final String UI_LIBRARY_CACHE_PURGE_SERVICE_USER = "ui-library-cache-service";
  private static final long serialVersionUID = 8442978263338882415L;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private transient UiLibraryCompilationService uiLibraryCompilationService;

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
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    return Collections.singletonList(getServiceCacheRootPath());
  }

  @Override
  public String getCachedOutput(String libraryPath, ScriptType scriptType, boolean minified)
      throws CacheRetrievalException {
    String cachedResourcePath = String.format("%s%s%s", getServiceCacheRootPath(), libraryPath,
        scriptType.getExtension());
    if (minified) {
      cachedResourcePath = String.format("%s%s.min%s", getServiceCacheRootPath(), libraryPath,
          scriptType.getExtension());
    }
    try {
      BaseFile file = getResourceAsType(cachedResourcePath, getServiceResourceResolver(),
          scriptType.getFileModelClass());
      return file.getFileContent();
    } catch (ModelAdaptionException | IOException e) {
      throw new CacheRetrievalException(e.getMessage());
    }
  }

  @Override
  public String getCachedOutput(FrontendLibrary library, ScriptType scriptType, boolean minified)
      throws CacheRetrievalException {
    return getCachedOutput(library.getPath(), scriptType, minified);
  }

  @Override
  public void cacheUiLibraryScript(String libraryPath, String content, ScriptType scriptType,
      boolean isMinified) throws CacheBuilderException {
    if (isMinified) {
      createCacheFile(content, String.format("%s.min%s", libraryPath, scriptType.getExtension()),
          scriptType);
    } else {
      createCacheFile(content, String.format("%s%s", libraryPath, scriptType.getExtension()),
          scriptType);
    }
  }
}