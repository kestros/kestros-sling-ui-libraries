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

package io.kestros.commons.uilibraries.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryJavaScriptServletTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private UiLibraryCacheService uiLibraryCacheService;
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  private UiLibraryJavaScriptServlet servlet;

  private final Map<String, Object> properties = new HashMap<>();


  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    uiLibraryConfigurationService = mock(UiLibraryConfigurationService.class);
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    context.registerService(UiLibraryConfigurationService.class, uiLibraryConfigurationService);
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenReturn("output");
    servlet = spy(new UiLibraryJavaScriptServlet());
  }

  @Test
  public void testDoGet() throws Exception {
    properties.put("jcr:primaryType", "kes:UiLibrary");

    final Resource resource = context.create().resource("/ui-library", properties);

    context.request().setResource(resource);

    when(servlet.getUiLibraryCacheService()).thenReturn(uiLibraryCacheService);
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    servlet.doGet(context.request(), context.response());

    assertEquals(200, context.response().getStatus());
    assertEquals("application/javascript", context.response().getContentType());
    assertEquals("output", context.response().getOutputAsString());
  }


}