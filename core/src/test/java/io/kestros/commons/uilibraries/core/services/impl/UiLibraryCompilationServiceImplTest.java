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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.JcrFileReadException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.ScriptFile;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.models.UiLibrary;
import io.kestros.commons.uilibraries.api.services.CssScriptTypeCompilerService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import io.kestros.commons.uilibraries.basecompilers.services.CssCompilerService;
import io.kestros.commons.uilibraries.basecompilers.services.JavaScriptCompilerService;
import io.kestros.commons.uilibraries.core.services.impl.sample.SampleCompilerService;
import io.kestros.commons.uilibraries.core.services.impl.sample.SampleScriptType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryCompilationServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryCompilationService compilationService;

  private CssCompilerService cssCompilerService;

  private JavaScriptCompilerService javaScriptCompilerService;
  private SampleCompilerService sampleCompilerService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    compilationService = new UiLibraryCompilationServiceImpl();
    cssCompilerService = spy(new CssCompilerService());
    javaScriptCompilerService = spy(new JavaScriptCompilerService());
    sampleCompilerService = new SampleCompilerService();
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("UI Library Compilation Service", compilationService.getDisplayName());
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = spy(new FormattingResultLog());

    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    compilationService.runAdditionalHealthChecks(log);

    verify(log, never()).debug(any());
    verify(log, never()).info(any());
    verify(log, never()).warn(any());
    verify(log, never()).critical(any());
    verify(log, never()).healthCheckError(any());
  }

  @Test
  public void testRunAdditionalHealthChecksWhenNoCompilers() {
    FormattingResultLog log = spy(new FormattingResultLog());

    context.registerInjectActivateService(compilationService);

    compilationService.runAdditionalHealthChecks(log);

    verify(log, never()).debug(any());
    verify(log, never()).info(any());
    verify(log, never()).warn(any());
    verify(log, times(2)).critical(any());
    verify(log, never()).healthCheckError(any());
  }

  @Test
  public void testGetCompilers() {
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    assertEquals(2, compilationService.getCompilers().size());
  }

  @Test
  public void testGetCompiler() throws NoMatchingCompilerException {
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    assertNotNull(compilationService.getCompiler(Collections.singletonList(
                    ScriptTypes.CSS),
            compilationService.getCompilers()));
    assertEquals(cssCompilerService,
            compilationService.getCompiler(Collections.singletonList(
                            ScriptTypes.CSS),
                    compilationService.getCompilers()));
    assertEquals(javaScriptCompilerService,
            compilationService.getCompiler(Collections.singletonList(
                            ScriptTypes.JAVASCRIPT),
                    compilationService.getCompilers()));
  }

  @Test
  public void testGetCompilerWhenNoMatchingCompiler() {
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    Exception exception = null;
    ScriptType scriptType = mock(ScriptType.class);
    when(scriptType.getName()).thenReturn("abc");
    try {
      compilationService.getCompiler(Collections.singletonList(scriptType),
              compilationService.getCompilers());
    } catch (NoMatchingCompilerException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("No compiler registered for ScriptType(s): abc.", exception.getMessage());
  }

  @Test
  public void testGetUiLibraryOutputWhenCss()
          throws InvalidResourceTypeException, NoMatchingCompilerException, IOException,
          JcrFileReadException {
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    UiLibrary uiLibrary = mock(UiLibrary.class);

    ScriptFile scriptFile1 = mock(ScriptFile.class);
    when(scriptFile1.getFileContent()).thenReturn("body{}");
    when(scriptFile1.getFileType()).thenReturn(
            ScriptTypes.CSS);
    ScriptFile scriptFile2 = mock(ScriptFile.class);
    when(scriptFile2.getFileContent()).thenReturn("p{}");
    when(scriptFile2.getFileType()).thenReturn(
            ScriptTypes.CSS);

    List<ScriptFile> scriptFileList = new ArrayList<>();
    scriptFileList.add(scriptFile1);
    scriptFileList.add(scriptFile2);

    when(uiLibrary.getScriptFiles(any(), eq("css"))).thenReturn(scriptFileList);

    assertEquals("body{}\np{}",
            compilationService.getUiLibraryOutput(uiLibrary, ScriptTypes.CSS));
    verify(cssCompilerService, times(1)).getOutput(any());
    verify(javaScriptCompilerService, never()).getOutput(any());
  }

  @Test
  public void testGetUiLibraryOutputWhenJavaScript()
          throws InvalidResourceTypeException, NoMatchingCompilerException, IOException,
          JcrFileReadException {
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    UiLibrary uiLibrary = mock(UiLibrary.class);

    ScriptFile scriptFile1 = mock(ScriptFile.class);
    when(scriptFile1.getFileContent()).thenReturn("console.log('1')");
    when(scriptFile1.getFileType()).thenReturn(
            ScriptTypes.JAVASCRIPT);
    ScriptFile scriptFile2 = mock(ScriptFile.class);
    when(scriptFile2.getFileContent()).thenReturn("console.log('2')");
    when(scriptFile2.getFileType()).thenReturn(
            ScriptTypes.JAVASCRIPT);

    List<ScriptFile> scriptFileList = new ArrayList<>();
    scriptFileList.add(scriptFile1);
    scriptFileList.add(scriptFile2);

    when(uiLibrary.getScriptFiles(any(), eq("js"))).thenReturn(scriptFileList);

    assertEquals("console.log('1')\nconsole.log('2')",
            compilationService.getUiLibraryOutput(uiLibrary, ScriptTypes.JAVASCRIPT));
    verify(cssCompilerService, never()).getOutput(any());
    verify(javaScriptCompilerService, times(1)).getOutput(any());
  }

  @Test
  public void testGetUiLibraryOutputWhenIoException()
          throws InvalidResourceTypeException, NoMatchingCompilerException, IOException,
          JcrFileReadException {
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    UiLibrary uiLibrary = mock(UiLibrary.class);

    ScriptFile scriptFile1 = mock(ScriptFile.class);
    when(scriptFile1.getFileContent()).thenReturn("body{}");
    when(scriptFile1.getFileType()).thenReturn(
            ScriptTypes.CSS);
    when(scriptFile1.getName()).thenReturn("file1.css");
    ScriptFile scriptFile2 = mock(ScriptFile.class);
    when(scriptFile2.getFileType()).thenReturn(
            ScriptTypes.CSS);
    when(scriptFile2.getName()).thenReturn("file2.css");
    when(scriptFile2.getFileContent()).thenThrow(new IOException());

    List<ScriptFile> scriptFileList = new ArrayList<>();
    scriptFileList.add(scriptFile1);
    scriptFileList.add(scriptFile2);

    when(uiLibrary.getScriptFiles(any(), eq("css"))).thenReturn(
            scriptFileList);

    assertEquals("body{}", compilationService.getUiLibraryOutput(uiLibrary, ScriptTypes.CSS));
    verify(cssCompilerService, times(1)).getOutput(any());
    verify(javaScriptCompilerService, never()).getOutput(any());
  }

  @Test
  public void testGetCompilerWhenNoLessFilesAreUsed() throws NoMatchingCompilerException {
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerService(CssScriptTypeCompilerService.class, sampleCompilerService);
    context.registerInjectActivateService(compilationService);

    assertEquals(cssCompilerService,
            compilationService.getCompiler(Arrays.asList(ScriptTypes.CSS),
                    Arrays.asList(cssCompilerService, sampleCompilerService)));
  }

  @Test
  public void testGetCompilerWhenNoLessFilesAreUsedAndSampleCompilerRegisteredFirst() throws
          NoMatchingCompilerException {
    context.registerService(CssScriptTypeCompilerService.class, sampleCompilerService);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    assertEquals(cssCompilerService,
            compilationService.getCompiler(Arrays.asList(ScriptTypes.CSS),
                    Arrays.asList(sampleCompilerService, cssCompilerService)));
  }


  @Test
  public void testGetCompilerWhenLessFilesAreUsed() throws NoMatchingCompilerException {
    context.registerInjectActivateService(cssCompilerService);
    context.registerService(CssScriptTypeCompilerService.class, sampleCompilerService);
    context.registerInjectActivateService(javaScriptCompilerService);
    context.registerInjectActivateService(compilationService);

    assertEquals(sampleCompilerService,
            compilationService.getCompiler(
                    Arrays.asList(ScriptTypes.CSS, SampleScriptType.SAMPLE_SCRIPT_TYPE),
                    Arrays.asList(cssCompilerService, sampleCompilerService)));
  }
}