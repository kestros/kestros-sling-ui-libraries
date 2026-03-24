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

package io.kestros.commons.uilibraries.lesscss.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LessCssCompilerServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private LessCssCompilerService lessCssCompilerService;

  @Before
  public void setUp() throws Exception {
    lessCssCompilerService = new LessCssCompilerService();
  }

  @Test
  public void testGetScriptTypes() {
    assertEquals(2, lessCssCompilerService.getScriptTypes().size());
    assertEquals(".css", lessCssCompilerService.getScriptTypes().get(0).getExtension());
    assertEquals(".less", lessCssCompilerService.getScriptTypes().get(1).getExtension());
  }

  @Test
  public void testGetOutput() {
    assertEquals("", lessCssCompilerService.getOutput(""));
  }

  @Test
  public void testGetOutputWhenException() {
    assertEquals(
            "<h1>Undefined mixin: adfsdf/.dsa.f/asd.f/ads.f/asd./f.asd/f.ads/f.asd/f/adf/dsa.f\n"
                    + "\t on line 1, column 60</h1><code>1:\tadfsdf/.dsa.f/asd.f/ads.f/asd./f"
                    + ".asd/f.ads/f.asd/f/adf/dsa.f<br></code>",
            lessCssCompilerService.getOutput(
                    "adfsdf/.dsa.f/asd.f/ads.f/asd./f.asd/f.ads/f.asd/f/adf/dsa.f"));
  }

  @Test
  public void testGetOutputWhenUsingHsl() {
    // jlessc does not support CSS Color Level 4 space-separated hsl() syntax.
    // Verify the compiler returns an error message rather than crashing.
    // TODO: upgrade Less compiler to support modern CSS hsl() syntax.
    String output = lessCssCompilerService.getOutput(""
                    + ":root {"
                    + "--color-white: hsl(0 0% 100%);"
                    + "}");
    assertTrue("Expected error output to contain h1 tag with error message",
            output.contains("<h1>"));
    assertTrue("Expected error to reference the unsupported operator",
            output.contains("Oprator"));
  }
}