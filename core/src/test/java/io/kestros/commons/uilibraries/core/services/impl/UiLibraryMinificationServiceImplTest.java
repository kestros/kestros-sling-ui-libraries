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

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.api.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.api.services.ScriptMinifierService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptType;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryMinificationServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryMinificationServiceImpl minificationService;

  private ScriptMinifierService scriptMinifierService1;
  private ScriptMinifierService scriptMinifierService2;
  private ScriptMinifierService scriptMinifierService3;

  @Before
  public void setUp() throws Exception {
    minificationService = new UiLibraryMinificationServiceImpl();
    scriptMinifierService1 = mock(ScriptMinifierService.class);
    scriptMinifierService2 = mock(ScriptMinifierService.class);
    scriptMinifierService3 = mock(ScriptMinifierService.class);

    when(scriptMinifierService1.getSupportedScriptTypes()).thenReturn(
        singletonList(ScriptType.CSS));
    when(scriptMinifierService2.getSupportedScriptTypes()).thenReturn(
        singletonList(ScriptType.CSS));
    when(scriptMinifierService3.getSupportedScriptTypes()).thenReturn(
        singletonList(ScriptType.JAVASCRIPT));
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("UI Library Minification Service", minificationService.getDisplayName());
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = spy(new FormattingResultLog());
    context.registerService(ScriptMinifierService.class, scriptMinifierService1);
    context.registerService(ScriptMinifierService.class, scriptMinifierService2);
    context.registerService(ScriptMinifierService.class, scriptMinifierService3);

    context.registerInjectActivateService(minificationService);

    minificationService.runAdditionalHealthChecks(log);

    verify(log, never()).debug(any());
    verify(log, never()).info(any());
    verify(log, never()).warn(any());
    verify(log, never()).critical(any());
    verify(log, never()).healthCheckError(any());
  }

  @Test
  public void testRunAdditionalHealthChecksWhenNoMinifiers() {
    FormattingResultLog log = spy(new FormattingResultLog());

    context.registerInjectActivateService(minificationService);

    minificationService.runAdditionalHealthChecks(log);

    verify(log, never()).debug(any());
    verify(log, never()).info(any());
    verify(log, times(2)).warn(any());
    verify(log, never()).critical(any());
    verify(log, never()).healthCheckError(any());
  }

  @Test
  public void testIsMinifiedRequestWhenDoesNotHaveMinSelector() {
    context.requestPathInfo().setResourcePath("/ui-library");
    context.requestPathInfo().setExtension("css");

    assertFalse(minificationService.isMinifiedRequest(context.request()));
  }

  @Test
  public void testIsMinifiedRequestWhenHasMinSelector() {
    context.requestPathInfo().setResourcePath("/ui-library");
    context.requestPathInfo().setSelectorString("min");
    context.requestPathInfo().setExtension("css");

    assertTrue(minificationService.isMinifiedRequest(context.request()));
  }

  @Test
  public void testGetMinificationServices() {
    context.registerService(ScriptMinifierService.class, scriptMinifierService1);
    context.registerService(ScriptMinifierService.class, scriptMinifierService2);
    context.registerService(ScriptMinifierService.class, scriptMinifierService3);

    context.registerInjectActivateService(minificationService);

    assertEquals(3, minificationService.getMinificationServices().size());
  }

  @Test
  public void testGetCssMinificationServices() {
    context.registerService(ScriptMinifierService.class, scriptMinifierService1);
    context.registerService(ScriptMinifierService.class, scriptMinifierService2);
    context.registerService(ScriptMinifierService.class, scriptMinifierService3);

    context.registerInjectActivateService(minificationService);

    assertEquals(2, minificationService.getCssMinificationServices().size());
  }

  @Test
  public void testGetJavaScriptMinificationServices() {
    context.registerService(ScriptMinifierService.class, scriptMinifierService1);
    context.registerService(ScriptMinifierService.class, scriptMinifierService2);
    context.registerService(ScriptMinifierService.class, scriptMinifierService3);

    context.registerInjectActivateService(minificationService);

    assertEquals(1, minificationService.getJavaScriptMinificationServices().size());
  }

  @Test
  public void testGetMinifiedOutput() throws ScriptCompressionException {
    context.registerService(ScriptMinifierService.class, scriptMinifierService1);
    context.registerService(ScriptMinifierService.class, scriptMinifierService2);
    context.registerService(ScriptMinifierService.class, scriptMinifierService3);

    context.registerInjectActivateService(minificationService);

    when(scriptMinifierService1.getMinifiedScript("unminified", ScriptType.CSS)).thenReturn("css-1-minified");
    when(scriptMinifierService3.getMinifiedScript("unminified", ScriptType.JAVASCRIPT)).thenReturn("js-1-minified");

    assertEquals("css-1-minified",
        minificationService.getMinifiedOutput("unminified", ScriptType.CSS));
    assertEquals("js-1-minified",
        minificationService.getMinifiedOutput("unminified", ScriptType.JAVASCRIPT));
  }

  @Test
  public void testGetMinifiedOutputWhenNoMinificationServices() throws ScriptCompressionException {
    context.registerInjectActivateService(minificationService);

    assertEquals("unminified",
        minificationService.getMinifiedOutput("unminified", ScriptType.CSS));
    assertEquals("unminified",
        minificationService.getMinifiedOutput("unminified", ScriptType.JAVASCRIPT));
  }
}