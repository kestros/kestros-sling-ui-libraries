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

package io.kestros.commons.uilibraries.core.healthchecks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import io.kestros.commons.uilibraries.api.services.UiLibraryConfigurationService;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryConfigurationServiceHealthCheckTest {
  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryConfigurationServiceHealthCheck healthCheck;

  private UiLibraryConfigurationService service;

  @Before
  public void setUp() throws Exception {
    service = mock(UiLibraryConfigurationService.class);
    healthCheck = new UiLibraryConfigurationServiceHealthCheck();

    context.registerService(UiLibraryConfigurationService.class, service);
    context.registerInjectActivateService(healthCheck);
  }

  @Test
  @Ignore
  public void testGetCacheService() {
    assertEquals(service, healthCheck.getManagedService());
  }

  @Test
  @Ignore
  public void testGetServiceName() {
    assertEquals("UI Library Configuration Service", healthCheck.getServiceName());
  }
}