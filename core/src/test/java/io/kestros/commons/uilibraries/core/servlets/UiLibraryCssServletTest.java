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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.api.exceptions.LibraryRetrievalException;
import io.kestros.commons.uilibraries.api.models.UiLibraryInterface;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryRetrievalService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryCssServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryCssServlet servlet;

  private UiLibraryCompilationService uiLibraryCompilationService;

  private UiLibraryConfigurationService uiLibraryConfigurationService;

  private UiLibraryMinificationService uiLibraryMinificationService;

  private UiLibraryCacheService uiLibraryCacheService;

  private UiLibraryRetrievalService uiLibraryRetrievalService;

  @Before
  public void setUp() throws Exception {
    servlet = new UiLibraryCssServlet();

    uiLibraryCompilationService = mock(UiLibraryCompilationService.class);
    uiLibraryConfigurationService = mock(UiLibraryConfigurationService.class);
    uiLibraryMinificationService = mock(UiLibraryMinificationService.class);
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    uiLibraryRetrievalService = mock(UiLibraryRetrievalService.class);

    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);
    context.registerService(UiLibraryConfigurationService.class, uiLibraryConfigurationService);
    context.registerService(UiLibraryMinificationService.class, uiLibraryMinificationService);
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    context.registerService(UiLibraryRetrievalService.class, uiLibraryRetrievalService);

    context.registerInjectActivateService(servlet);
  }

  @Test
  public void testGetLibrary() throws LibraryRetrievalException {
    UiLibraryInterface uiLibrary = mock(UiLibraryInterface.class);
    when(uiLibraryRetrievalService.getUiLibrary("/ui-library")).thenReturn(uiLibrary);
    assertEquals(uiLibrary, servlet.getLibrary("/ui-library"));
  }

  @Test
  public void testGetLibraryWhenLibraryNotFound() throws LibraryRetrievalException {
    when(uiLibraryRetrievalService.getUiLibrary("/ui-library")).thenThrow(
        new LibraryRetrievalException("message"));
    assertNull(servlet.getLibrary("/ui-library"));
  }

  @Test
  public void testGetUiLibraryCompilationService() {
    assertEquals(uiLibraryCompilationService, servlet.getUiLibraryCompilationService());
  }

  @Test
  public void testGetUiLibraryConfigurationService() {

    assertEquals(uiLibraryConfigurationService, servlet.getUiLibraryConfigurationService());
  }

  @Test
  public void testGetUiLibraryMinificationService() {
    assertEquals(uiLibraryMinificationService, servlet.getUiLibraryMinificationService());
  }

  @Test
  public void testGetUiLibraryCacheService() {
    assertEquals(uiLibraryCacheService, servlet.getUiLibraryCacheService());
  }

  @Test
  public void testGetScriptType() {
    assertEquals(ScriptType.CSS, servlet.getScriptType());
  }
}