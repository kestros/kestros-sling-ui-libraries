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

package io.kestros.commons.uilibraries.config;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.config.UiLibraryConfigurationFactory.Config;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

public class UiLibraryConfigurationFactoryTest {

  private UiLibraryConfigurationFactory uiLibraryConfigurationFactory;

  private Config config;

  @Before
  public void setUp() throws Exception {
    config = mock(Config.class);
    uiLibraryConfigurationFactory = new UiLibraryConfigurationFactory();
  }

  @Test
  public void testActivate() throws Exception {
    when(config.minifiedLibraryPaths()).thenReturn(new String[]{});

    uiLibraryConfigurationFactory.activate(config, new HashMap<>());
  }

  @Test
  public void testGetMinifiedLibraryPaths() {
    when(config.minifiedLibraryPaths()).thenReturn(new String[]{"item-1", "item-2"});
    uiLibraryConfigurationFactory.activate(config, new HashMap<>());
    assertEquals(2, uiLibraryConfigurationFactory.getMinifiedLibraryPaths().size());
    assertEquals("item-1", uiLibraryConfigurationFactory.getMinifiedLibraryPaths().get(0));
    assertEquals("item-2", uiLibraryConfigurationFactory.getMinifiedLibraryPaths().get(1));
  }
}