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

package io.kestros.commons.uilibraries.utils;

import io.kestros.commons.uilibraries.UiLibrary;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;

public class UiLibraryCacheUtilsTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private UiLibrary uiLibrary;

  private Resource resource;

  private final Map<String, Object> properties = new HashMap<>();
  private final Map<String, Object> uiLibraryProperties = new HashMap<>();
  private final Map<String, Object> cssRootProperties = new HashMap<>();
  private final Map<String, Object> fileProperties = new HashMap<>();
  private final Map<String, Object> importerContentProperties = new HashMap<>();
  private final Map<String, Object> importedContentProperties = new HashMap<>();

  @Before
  public void setup() {
    context.addModelsForPackage("io.kestros.commons.uilibraries");
  }

  //
  //  @Test
  //  public void testCacheScripts()
  //      throws UnsupportedEncodingException, ResourceNotFoundException, PersistenceException {
  //    resource = context.create().resource("/ui-library", properties);
  //
  //    uiLibrary = resource.adaptTo(UiLibrary.class);
  //
  //    cacheScripts(uiLibrary, false);
  //
  //    assertNotNull(context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library
  //    .css"));
  //    assertNotNull(context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library
  //    .js"));
  //
  //    assertNull(
  //        context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library.min.css"));
  //    assertNull(context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library.min
  //    .js"));
  //  }
  //
  //  @Test
  //  public void testCacheScriptsWhenCacheMinified()
  //      throws UnsupportedEncodingException, ResourceNotFoundException, PersistenceException {
  //    resource = context.create().resource("/ui-library", properties);
  //
  //    uiLibrary = resource.adaptTo(UiLibrary.class);
  //
  //    cacheScripts(uiLibrary, true);
  //
  //    assertNotNull(context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library
  //    .css"));
  //    assertNotNull(context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library
  //    .js"));
  //
  //    assertNotNull(
  //        context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library.min.css"));
  //    assertNotNull(
  //        context.resourceResolver().getResource("/var/cache/ui-libraries/ui-library.min.js"));
  //  }
  //
  //
  //
  //  @Test
  //  public void testCacheScriptsWhenCacheMinifiedAndJavaScriptMinificationThrowsException()
  //      throws IOException, ResourceNotFoundException {
  //    resource = context.create().resource("/ui-library", properties);
  //
  //    uiLibrary = resource.adaptTo(UiLibrary.class);
  //
  //    uiLibrary = spy(uiLibrary);
  //
  //    doReturn("%%bad-js%%").when(uiLibrary).getOutput(JAVASCRIPT);
  //
  //    cacheScripts(uiLibrary, true);
  //
  //    JavaScriptFile minifiedJsFile = context.resourceResolver()
  //        .getResource("/var/cache/ui-libraries/ui-library.min.js").adaptTo(JavaScriptFile.class);
  //
  //    assertEquals("%%bad-js%%", minifiedJsFile.getOutput());
  //  }
  //
  //  @Test
  //  public void testCreateResourcesFromPath() throws ResourceNotFoundException,
  //  PersistenceException {
  //    UiLibraryCacheUtils.createResourcesFromPath("/var/cache/test", context.resourceResolver());
  //
  //    assertNotNull(context.resourceResolver().getResource("/var/cache/test"));
  //  }
  //
  //  @Test
  //  public void testCacheScriptsWhenCacheUnminified()
  //      throws IOException, ResourceNotFoundException {
  //    resource = context.create().resource("/ui-library", properties);
  //
  //    uiLibrary = resource.adaptTo(UiLibrary.class);
  //
  //    uiLibrary = spy(uiLibrary);
  //
  //    doReturn("uncompressed").when(uiLibrary).getOutput(JAVASCRIPT);
  //
  //    cacheScripts(uiLibrary, true);
  //
  //    JavaScriptFile jsFile = context.resourceResolver()
  //        .getResource("/var/cache/ui-libraries/ui-library.js").  adaptTo(JavaScriptFile.class);
  //
  //    assertEquals("uncompressed", jsFile.getOutput());
  //  }

}