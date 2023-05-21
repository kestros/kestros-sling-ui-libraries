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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.api.exceptions.LibraryRetrievalException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryRetrievalServiceImpl uiLibraryRetrievalService;

  private ResourceResolverFactory resourceResolverFactory;

  @Before
  public void setup() throws LoginException {
    context.addModelsForPackage("io.kestros");
    uiLibraryRetrievalService = spy(new UiLibraryRetrievalServiceImpl());

    resourceResolverFactory = mock(ResourceResolverFactory.class);

    Map<String, Object> properties = new HashMap<>();
    properties.put("jcr:primaryType", "kes:UiLibrary");
    context.create().resource("/etc/ui-library", properties);

    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
  }

  @Test
  public void testGetUiLibrary() throws LibraryRetrievalException {
    doReturn(resourceResolverFactory).when(uiLibraryRetrievalService).getResourceResolverFactory();
    context.registerInjectActivateService(uiLibraryRetrievalService);

    assertEquals("/etc/ui-library",
        uiLibraryRetrievalService.getUiLibrary("/etc/ui-library").getPath());
  }

  @Test
  public void testGetUiLibraryWhenLibraryNotFound() {
    doReturn(resourceResolverFactory).when(uiLibraryRetrievalService).getResourceResolverFactory();
    context.registerInjectActivateService(uiLibraryRetrievalService);

    Exception exception = null;
    try {
      uiLibraryRetrievalService.getUiLibrary("/etc/ui-library-missing");
    } catch (LibraryRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("Unable to adapt '/etc/ui-library-missing': Resource not found.",
        exception.getMessage());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("ui-library-manager", uiLibraryRetrievalService.getServiceUserName());
  }

  @Test
  public void testGetResourceResolverFactory() {
    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
    context.registerInjectActivateService(uiLibraryRetrievalService);
    assertNotNull(uiLibraryRetrievalService.getResourceResolverFactory());
  }

  @Test
  public void tesGetDisplayName() {
    assertEquals("UI Library Retrieval Service", uiLibraryRetrievalService.getDisplayName());
  }

  @Test
  public void testDeactivate() {
    uiLibraryRetrievalService.deactivate(context.componentContext());
  }

}