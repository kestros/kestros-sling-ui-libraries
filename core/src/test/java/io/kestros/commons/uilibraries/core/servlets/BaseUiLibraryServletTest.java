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

package io.kestros.commons.uilibraries.core.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.LibraryRetrievalException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.services.ScriptTypeCompiler;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryRetrievalService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import io.kestros.commons.uilibraries.basecompilers.services.CssCompilerService;
import io.kestros.commons.uilibraries.core.UiLibraryResource;
import io.kestros.commons.uilibraries.core.services.impl.UiLibraryCompilationServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseUiLibraryServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryCssServlet servlet;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> cssFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> cssFileProperties = new HashMap<>();

  private UiLibraryCompilationService uiLibraryCompilationService;

  private UiLibraryRetrievalService uiLibraryRetrievalService;

  private UiLibraryCacheService uiLibraryCacheService;

  private UiLibraryMinificationService uiLibraryMinificationService;

  private CssCompilerService cssCompilerService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    servlet = new UiLibraryCssServlet();

    uiLibraryCompilationService = spy(new UiLibraryCompilationServiceImpl());
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    uiLibraryRetrievalService = mock(UiLibraryRetrievalService.class);
    uiLibraryMinificationService = mock(UiLibraryMinificationService.class);
    cssCompilerService = new CssCompilerService();

    final InputStream inputStream = new ByteArrayInputStream("css-output".getBytes());

    fileProperties.put("jcr:primaryType", "nt:file");
    cssFileProperties.put("jcr:primaryType", "nt:resource");
    cssFileProperties.put("jcr:mimeType", "text/css");
    cssFileProperties.put("jcr:data", inputStream);
  }

  @Test
  public void testDoGet()
      throws LibraryRetrievalException, InvalidResourceTypeException, NoMatchingCompilerException,
             CacheBuilderException {
    properties.put("jcr:primaryType", "kes:UiLibrary");
    resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file-1.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file-1.css", fileProperties);
    context.create().resource("/ui-library/css/file-1.css/jcr:content", cssFileProperties);

    UiLibraryResource uiLibrary = resource.adaptTo(UiLibraryResource.class);
    context.request().setResource(resource);

    when(uiLibraryRetrievalService.getUiLibrary("/ui-library", context.resourceResolver())).thenReturn(uiLibrary);

    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    context.registerService(UiLibraryRetrievalService.class, uiLibraryRetrievalService);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(servlet);

    servlet.doGet(context.request(), context.response());
    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("css-output", context.response().getOutputAsString());
    verify(uiLibraryCompilationService, times(1)).getUiLibraryOutput(any(), any(), any());
    verify(uiLibraryCacheService, times(1)).cacheUiLibraryScript("/ui-library", "css-output",
        ScriptTypes.CSS, false);
  }

  @Test
  public void testDoGetWhenCachedOutputIsRetrieved()
      throws LibraryRetrievalException, InvalidResourceTypeException, NoMatchingCompilerException,
             CacheRetrievalException, CacheBuilderException {

    properties.put("jcr:primaryType", "kes:UiLibrary");
    resource = context.create().resource("/ui-library", properties);
    context.request().setResource(resource);

    when(uiLibraryCacheService.getCachedOutput("/ui-library", ScriptTypes.CSS, false)).thenReturn(
        "cached-output");

    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    context.registerService(UiLibraryRetrievalService.class, uiLibraryRetrievalService);
    context.registerService(ScriptTypeCompiler.class, cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(servlet);

    servlet.doGet(context.request(), context.response());
    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("cached-output", context.response().getOutputAsString());
    verify(uiLibraryCompilationService, never()).getUiLibraryOutput(any(), any());
    verify(uiLibraryCacheService, times(1)).getCachedOutput("/ui-library", ScriptTypes.CSS, false);
  }

  @Test
  public void testDoGetWhenCachedOutputIsRetrievedAndMinified()
      throws LibraryRetrievalException, InvalidResourceTypeException, NoMatchingCompilerException,
             CacheRetrievalException, CacheBuilderException {

    properties.put("jcr:primaryType", "kes:UiLibrary");
    resource = context.create().resource("/ui-library", properties);
    context.request().setResource(resource);

    when(uiLibraryCacheService.getCachedOutput("/ui-library", ScriptTypes.CSS, true)).thenReturn(
        "cached-minified-output");

    context.registerService(UiLibraryMinificationService.class, uiLibraryMinificationService);
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    context.registerService(UiLibraryRetrievalService.class, uiLibraryRetrievalService);
    context.registerService(ScriptTypeCompiler.class, cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(servlet);

    when(uiLibraryMinificationService.isMinifiedRequest(any())).thenReturn(true);

    servlet.doGet(context.request(), context.response());
    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("cached-minified-output", context.response().getOutputAsString());
    verify(uiLibraryCompilationService, never()).getUiLibraryOutput(any(), any());
    verify(uiLibraryCacheService, times(1)).getCachedOutput("/ui-library", ScriptTypes.CSS, true);
  }

  @Test
  public void testDoGetWhenWhenCompilationError()
      throws LibraryRetrievalException, InvalidResourceTypeException, NoMatchingCompilerException,
             CacheRetrievalException {
    properties.put("jcr:primaryType", "kes:UiLibrary");
    resource = context.create().resource("/ui-library", properties);

    UiLibraryResource uiLibrary = resource.adaptTo(UiLibraryResource.class);
    context.request().setResource(resource);

    when(uiLibraryRetrievalService.getUiLibrary("/ui-library", context.resourceResolver())).thenReturn(uiLibrary);

    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    context.registerService(UiLibraryRetrievalService.class, uiLibraryRetrievalService);
    context.registerService(ScriptTypeCompiler.class, cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(servlet);

    doThrow(new NoMatchingCompilerException("")).when(
        uiLibraryCompilationService).getUiLibraryOutput(any(), any(),any());

    servlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
    assertEquals("text/plain", context.response().getContentType());
    assertEquals("", context.response().getOutputAsString());
    verify(uiLibraryCompilationService, times(1)).getUiLibraryOutput(any(), any(), any());
  }

  @Test
  public void testCacheOutput() {
  }

  @Test
  public void testGetCachedOutputOrEmptyString() {
  }

  @Test
  public void testWriteResponse() {
  }
}