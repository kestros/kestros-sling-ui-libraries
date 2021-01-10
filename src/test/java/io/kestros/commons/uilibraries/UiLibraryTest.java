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

package io.kestros.commons.uilibraries;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.services.compilation.impl.UiLibraryCompilationServiceImpl;
import io.kestros.commons.uilibraries.services.minification.UiLibraryMinificationService;
import io.kestros.commons.uilibraries.services.minification.impl.BaseUiLibraryMinificationService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibrary uiLibrary;
  private UiLibraryCacheService uiLibraryCacheService;
  private UiLibraryCompilationService uiLibraryCompilationService;
  private UiLibraryMinificationService minificationService;

  private Resource resource;

  private final Map<String, Object> properties = new HashMap<>();

  private final Map<String, Object> cssFolderProperties = new HashMap<>();
  private final Map<String, Object> jsFolderProperties = new HashMap<>();

  private final Map<String, Object> fileProperties = new HashMap<>();
  private final Map<String, Object> fileContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros.commons.uilibraries");
    properties.put("jcr:primaryType", "kes:UiLibrary");

    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    minificationService = new BaseUiLibraryMinificationService();
    uiLibraryCompilationService = new UiLibraryCompilationServiceImpl();

    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    fileProperties.put("jcr:primaryType", "nt:file");
  }

  @Test
  public void testGetCssPath() throws Exception {
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("/ui-library.css", uiLibrary.getCssPath());
  }

  @Test
  public void testGetJsPath() throws Exception {
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("/ui-library.js", uiLibrary.getJsPath());
  }
  //
  //  @Test
  //  public void testGetCachedOrUncachedOutputCssOutputWhenUiLibraryCacheException()
  //      throws CacheRetrievalException {
  //    resource = context.create().resource("/ui-library", properties);
  //
  //    uiLibrary = resource.adaptTo(UiLibrary.class);
  //    uiLibrary = spy(uiLibrary);
  //
  //    doThrow(new CacheRetrievalException("")).when(uiLibraryCacheService).getCachedOutput
  //    (uiLibrary,
  //        CSS, false);
  //    doReturn("cached-compressed").when(uiLibrary).getOutput(CSS, true);
  //    doReturn("uncached-uncompressed").when(uiLibrary).getOutput(CSS, false);
  //    doReturn("uncached-compressed").when(uiLibrary).getOutput(CSS, true);
  //
  //    assertEquals("uncached-uncompressed", uiLibrary.getOutput(CSS, false));
  //  }
  //
  //  @Test
  //  public void testGetCachedOrUncachedOutputCssOutputWhenUiLibraryCacheExceptionAndMinified()
  //      throws CacheRetrievalException {
  //    resource = context.create().resource("/ui-library", properties);
  //
  //    uiLibrary = resource.adaptTo(UiLibrary.class);
  //    uiLibrary = spy(uiLibrary);
  //
  //    doThrow(new CacheRetrievalException("")).when(uiLibraryCacheService).getCachedOutput
  //    (uiLibrary,
  //        CSS, true);
  //
  //    doReturn("cached-uncompressed").when(uiLibrary).getOutput(CSS, false);
  //    doReturn("uncached-uncompressed").when(uiLibrary).getOutput(CSS, false);
  //    doReturn("uncached-compressed").when(uiLibrary).getOutput(CSS, true);
  //
  //    assertEquals("uncached-compressed", uiLibrary.getOutput(CSS, true));
  //  }

  @Test
  public void testGetDependencies() {
    context.create().resource("/etc/ui-library-1", properties);
    context.create().resource("/etc/ui-library-2", properties);

    properties.put("dependencies", new String[]{"/etc/ui-library-1", "/etc/ui-library-2"});
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);
    uiLibrary = spy(uiLibrary);

    assertEquals(2, uiLibrary.getDependencies().size());
    assertEquals("/etc/ui-library-1", uiLibrary.getDependencies().get(0).getPath());
    assertEquals("/etc/ui-library-2", uiLibrary.getDependencies().get(1).getPath());
  }

  @Test
  public void testGetDependenciesWhenDependencyDoesNotExist() {
    properties.put("dependencies", new String[]{"/etc/ui-library-1", "/etc/ui-library-2"});
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);
    uiLibrary = spy(uiLibrary);

    assertEquals(0, uiLibrary.getDependencies().size());
  }

  @Test
  public void testGetDependenciesWhenDependencyIsInvalidResourceType() {
    context.create().resource("/etc/ui-library-1");
    context.create().resource("/etc/ui-library-2");

    properties.put("dependencies", new String[]{"/etc/ui-library-1", "/etc/ui-library-2"});
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);
    uiLibrary = spy(uiLibrary);

    assertEquals(0, uiLibrary.getDependencies().size());
  }

  @Test
  public void testGetOutputWhenCss() throws CacheRetrievalException, InvalidResourceTypeException {
    context.registerService(UiLibraryMinificationService.class, minificationService);
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    resource = context.create().resource("/ui-library", properties);

    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doThrow(new CacheRetrievalException("")).when(uiLibraryCacheService).getCachedOutput(uiLibrary,
        CSS, false);

    assertEquals("body{ color:red;}\n", uiLibrary.getOutput(LESS, false));
  }

  @Test
  public void testGetOutputWhenCssAndMinified()
      throws CacheRetrievalException, InvalidResourceTypeException {
    context.registerService(UiLibraryMinificationService.class, minificationService);
    context.registerInjectActivateService(uiLibraryCompilationService);

    resource = context.create().resource("/ui-library", properties);

    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream(
        "body {\n\tcolor:red;\n}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doThrow(new CacheRetrievalException("")).when(uiLibraryCacheService).getCachedOutput(uiLibrary,
        CSS, false);

    assertEquals("body{color:red}", uiLibrary.getOutput(LESS, true));
  }

  @Test
  public void testGetOutputWhenCssAndMinificationServiceIsNull()
      throws CacheRetrievalException, InvalidResourceTypeException {
    resource = context.create().resource("/ui-library", properties);
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doThrow(new CacheRetrievalException("")).when(uiLibraryCacheService).getCachedOutput(uiLibrary,
        CSS, false);

    assertEquals("body{ color:red;}\n", uiLibrary.getOutput(CSS, true));
    assertEquals("body{ color:red;}\n", uiLibrary.getOutput(LESS, true));
  }

  @Test
  public void testGetCssOutputWhenNoCssResource()
      throws CacheRetrievalException, InvalidResourceTypeException {
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doThrow(new CacheRetrievalException("")).when(uiLibraryCacheService).getCachedOutput(uiLibrary,
        CSS, false);

    assertEquals("", uiLibrary.getOutput(CSS, false));
  }

  @Test
  public void testGetJavaScriptOutputWhenNoJsResource()
      throws CacheRetrievalException, InvalidResourceTypeException {
    context.registerService(UiLibraryMinificationService.class, minificationService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doThrow(new CacheRetrievalException("")).when(uiLibraryCacheService).getCachedOutput(uiLibrary,
        JAVASCRIPT, false);

    assertEquals("", uiLibrary.getOutput(JAVASCRIPT, false));
  }

  @Test
  public void testGetDependencyPaths() {
    context.create().resource("/etc/ui-library-1", properties);
    context.create().resource("/etc/ui-library-2", properties);

    properties.put("dependencies", new String[]{"/etc/ui-library-1", "/etc/ui-library-2"});
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals(2, uiLibrary.getDependencyPaths().size());
    assertEquals("/etc/ui-library-1", uiLibrary.getDependencyPaths().get(0));
    assertEquals("/etc/ui-library-2", uiLibrary.getDependencyPaths().get(1));
  }

  @Test
  public void testGetDependenciesWithMultipleCalls() {
    context.create().resource("/etc/ui-library-1", properties);
    context.create().resource("/etc/ui-library-2", properties);

    properties.put("dependencies", new String[]{"/etc/ui-library-1", "/etc/ui-library-2"});

    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);
    uiLibrary = spy(uiLibrary);

    assertEquals(2, uiLibrary.getDependencies().size());
    assertEquals(2, uiLibrary.getDependencies().size());
    assertEquals(2, uiLibrary.getDependencies().size());
    assertEquals("/etc/ui-library-1", uiLibrary.getDependencies().get(0).getPath());
    assertEquals("/etc/ui-library-2", uiLibrary.getDependencies().get(1).getPath());
  }

  @Test
  public void testGetSupportedScriptTypes() {
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals(3, uiLibrary.getSupportedScriptTypes().size());
    assertEquals(JAVASCRIPT, uiLibrary.getSupportedScriptTypes().get(0));
    assertEquals(CSS, uiLibrary.getSupportedScriptTypes().get(1));
    assertEquals(LESS, uiLibrary.getSupportedScriptTypes().get(2));
  }

  @Test
  public void testGetSupportedScriptTypesWhenHasLessFiles() throws InvalidResourceTypeException {
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    final InputStream inputStream = new ByteArrayInputStream("div{\n\tcolor: red;\n}".getBytes());

    fileContentProperties.put("jcr:data", inputStream);
    fileContentProperties.put("jcr:mimeType", "text/less");

    resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file.less"});

    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.less", fileProperties);

    fileContentProperties.put("jcr:mimeType", "text/less");

    context.create().resource("/ui-library/css/file.less/jcr:content", fileContentProperties);
    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals(2, uiLibrary.getSupportedScriptTypes().size());
    assertEquals(JAVASCRIPT, uiLibrary.getSupportedScriptTypes().get(0));
    assertEquals(LESS, uiLibrary.getSupportedScriptTypes().get(1));
    assertEquals("div{\n" + "\tcolor: red;\n" + "}", uiLibrary.getOutput(LESS, false));
  }

  @Test
  public void testGetSupportedScriptTypesWhenHasLessFilesWithTextCssMimeType()
      throws InvalidResourceTypeException {
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    final InputStream inputStream = new ByteArrayInputStream("div{\n\tcolor: red;\n}".getBytes());

    fileContentProperties.put("jcr:data", inputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file.less"});

    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.less", fileProperties);

    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.less/jcr:content", fileContentProperties);
    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals(2, uiLibrary.getSupportedScriptTypes().size());
    assertEquals(JAVASCRIPT, uiLibrary.getSupportedScriptTypes().get(0));
    assertEquals(LESS, uiLibrary.getSupportedScriptTypes().get(1));
    assertEquals("div{\n" + "\tcolor: red;\n" + "}", uiLibrary.getOutput(LESS, false));
  }

  @Test
  public void testGetSupportedScriptTypesWhenHasLessAndCssFiles() {
    resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file.less", "file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);

    context.create().resource("/ui-library/css/file.less", fileProperties);
    fileContentProperties.put("jcr:mimeType", "text/less");
    context.create().resource("/ui-library/css/file.less/jcr:content", fileContentProperties);

    context.create().resource("/ui-library/css/file.css", fileProperties);
    fileContentProperties.put("jcr:mimeType", "text/css");
    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals(2, uiLibrary.getSupportedScriptTypes().size());
    assertEquals(JAVASCRIPT, uiLibrary.getSupportedScriptTypes().get(0));
    assertEquals(LESS, uiLibrary.getSupportedScriptTypes().get(1));
  }

}