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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.compilation.impl.UiLibraryCompilationServiceImpl;
import io.kestros.commons.uilibraries.services.minification.UiLibraryMinificationService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class JcrFileUiLibraryCacheServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private JcrFileUiLibraryCacheService cacheService;
  private UiLibraryCompilationServiceImpl uiLibraryCompilationService;
  private UiLibraryMinificationService uiLibraryMinificationService;
  private ResourceResolverFactory resourceResolverFactory;
  private JobManager jobManager;
  private UiLibrary uiLibrary;

  private Resource resource;
  private final Map<String, Object> uiLibraryProperties = new HashMap<>();


  @Before
  public void setUp() throws Exception {
    cacheService = new JcrFileUiLibraryCacheService();
    resourceResolverFactory = mock(ResourceResolverFactory.class);
    jobManager = mock(JobManager.class);
    uiLibraryCompilationService = new UiLibraryCompilationServiceImpl();
    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
    context.registerService(JobManager.class, jobManager);
    context.registerInjectActivateService(cacheService);
    context.registerInjectActivateService(uiLibraryCompilationService);

    context.create().resource("/var/cache/ui-libraries");
  }

  @Test
  public void testGetServiceCacheRootPath() {
    assertEquals("/var/cache/ui-libraries", cacheService.getServiceCacheRootPath());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("ui-library-cache-service", cacheService.getServiceUserName());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Ui Library Cache Service", cacheService.getDisplayName());
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(cacheService.getResourceResolverFactory());
  }

  @Test
  public void testGetCachedOutput() throws CacheRetrievalException {
    resource = context.create().resource("/etc/ui-library", uiLibraryProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);
    cacheService.cacheUiLibraryScripts(uiLibrary, true);
    assertNotNull(
        context.resourceResolver().getResource("/var/cache/ui-libraries/etc/ui-library.css"));
    assertEquals("", cacheService.getCachedOutput(uiLibrary, ScriptType.CSS, true));
  }

  @Test
  public void testCacheUiLibraryScripts() {
    resource = context.create().resource("/etc/ui-library", uiLibraryProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);
    cacheService.cacheUiLibraryScripts(uiLibrary, true);
    assertNotNull(
        context.resourceResolver().getResource("/var/cache/ui-libraries/etc/ui-library.css"));
  }


  @Test
  public void testGetScriptFileSuffix() {
    assertEquals(".css", JcrFileUiLibraryCacheService.getScriptFileSuffix(ScriptType.CSS, false));
    assertEquals(".min.css",
        JcrFileUiLibraryCacheService.getScriptFileSuffix(ScriptType.CSS, true));
    assertEquals(".js",
        JcrFileUiLibraryCacheService.getScriptFileSuffix(ScriptType.JAVASCRIPT, false));
    assertEquals(".min.js",
        JcrFileUiLibraryCacheService.getScriptFileSuffix(ScriptType.JAVASCRIPT, true));
  }
}