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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.core.eventlisteners.UiLibraryCachePurgeEventListener;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryCachePurgeEventListenerTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryCachePurgeEventListener eventListener;
  private UiLibraryCacheService cacheService1;
  private UiLibraryCacheService cacheService2;
  private UiLibraryCacheService cacheService3;

  @Before
  public void setup() {
    eventListener = new UiLibraryCachePurgeEventListener();
    cacheService1 = mock(UiLibraryCacheService.class);
    cacheService2 = mock(UiLibraryCacheService.class);
    cacheService3 = mock(UiLibraryCacheService.class);

    context.registerService(UiLibraryCacheService.class, cacheService1);
    context.registerService(UiLibraryCacheService.class, cacheService2);
    context.registerService(UiLibraryCacheService.class, cacheService3);

    context.registerInjectActivateService(eventListener);
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("UI Library Cache Purge Event Listener", eventListener.getDisplayName());
  }

  @Test
  public void testGetCacheServices() {
    assertEquals(3, eventListener.getCacheServices().size());
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(eventListener.getResourceResolverFactory());
  }

}
