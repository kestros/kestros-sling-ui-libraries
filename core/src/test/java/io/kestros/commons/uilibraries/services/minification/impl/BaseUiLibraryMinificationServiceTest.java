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

package io.kestros.commons.uilibraries.services.minification.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.kestros.commons.uilibraries.exceptions.CssCompressionException;
import io.kestros.commons.uilibraries.exceptions.JavaScriptCompressionException;
import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.minification.impl.BaseUiLibraryMinificationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class BaseUiLibraryMinificationServiceTest {

  private BaseUiLibraryMinificationService minificationService;

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    minificationService = new BaseUiLibraryMinificationService();
  }

  @Test
  public void testGetMinifiedOutputWhenCss() throws ScriptCompressionException {
    assertEquals("body{color:red}",
        minificationService.getMinifiedOutput("\nbody {\ncolor: red;\n}", ScriptType.CSS));
  }

  @Test
  public void testGetMinifiedOutputWhenCssAndCssCompressionException() {
    try {
      minificationService.getMinifiedOutput(null, ScriptType.CSS);
    } catch (final JavaScriptCompressionException e) {
    } catch (final CssCompressionException e) {
      exception = e;
    }
    assertEquals("Unable to minify css script.", exception.getMessage());
  }

  @Test
  public void testGetMinifiedOutputWhenJavascript() throws ScriptCompressionException {
    assertEquals("console.log(\"test\");console.log(\"test\");",
        minificationService.getMinifiedOutput("console.log('test');\n\nconsole.log('test');\n\n",
            ScriptType.JAVASCRIPT));
  }

  @Test
  public void testGetMinifiedOutputWhenJavascriptAndJavascriptCompressionException() {
    try {
      minificationService.getMinifiedOutput("", ScriptType.JAVASCRIPT);
    } catch (final JavaScriptCompressionException e) {
      exception = e;
    } catch (final CssCompressionException e) {
    }
    assertTrue(StringUtils.isNotEmpty(exception.getMessage()));
  }
}