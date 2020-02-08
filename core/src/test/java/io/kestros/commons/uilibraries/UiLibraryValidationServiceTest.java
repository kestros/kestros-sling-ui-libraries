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

package io.kestros.commons.uilibraries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryValidationService uiLibraryValidationService;
  private UiLibrary uiLibrary;
  private Resource resource;

  private final Map<String, Object> properties = new HashMap<>();
  private final Map<String, Object> cssFolderProperties = new HashMap<>();
  private final Map<String, Object> jsFolderProperties = new HashMap<>();

  private final Map<String, Object> fileProperties = new HashMap<>();
  private final Map<String, Object> fileContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    uiLibraryValidationService = spy(new UiLibraryValidationService());
    fileProperties.put("jcr:primaryType", "nt:file");

    properties.put("sling:resourceType", "kestros/commons/ui-library");
    properties.put("jcr:title", "title");
    properties.put("jcr:description", "description");
  }

  @Test
  public void testGetModel() {
    resource = context.create().resource("/ui-library");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    assertEquals("/ui-library", uiLibraryValidationService.getModel().getPath());
  }

  @Test
  public void testRegisterBasicValidators() {
    resource = context.create().resource("/ui-library", properties);
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertEquals(5, uiLibraryValidationService.getBasicValidators().size());
    assertTrue(uiLibraryValidationService.getBasicValidators().get(0).isValid());
    assertTrue(uiLibraryValidationService.getBasicValidators().get(1).isValid());
    assertEquals("Title is configured.",
        uiLibraryValidationService.getBasicValidators().get(0).getMessage());
    assertEquals("Description is configured.",
        uiLibraryValidationService.getBasicValidators().get(1).getMessage());
    assertEquals("One of the following is true:",
        uiLibraryValidationService.getBasicValidators().get(2).getMessage());
    assertEquals("All of the following are true:",
        uiLibraryValidationService.getBasicValidators().get(3).getMessage());
    assertEquals("All dependencies exist and are valid.",
        uiLibraryValidationService.getBasicValidators().get(4).getMessage());
  }

  @Test
  public void testRegisterDetailedValidators() {
    resource = context.create().resource("/ui-library");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerDetailedValidators();

    assertEquals(0, uiLibraryValidationService.getDetailedValidators().size());
  }

  @Test
  public void testHasCssOrJsDirectory() {
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/css");
    context.create().resource("/ui-library/js");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertTrue(uiLibraryValidationService.hasCssOrJsDirectory().isValid());
  }

  @Test
  public void testHasCssOrJsDirectoryWhenHasCssDirectory() {
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/css");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertTrue(uiLibraryValidationService.hasCssOrJsDirectory().isValid());
  }

  @Test
  public void testHasCssOrJsDirectoryWhenHasJsDirectory() {
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/js");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertTrue(uiLibraryValidationService.hasCssOrJsDirectory().isValid());
  }

  @Test
  public void testHasCssOrJsDirectoryWhenHasNeither() {
    resource = context.create().resource("/ui-library");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertFalse(uiLibraryValidationService.hasCssOrJsDirectory().isValid());
    assertEquals(ModelValidationMessageType.ERROR,
        uiLibraryValidationService.hasCssOrJsDirectory().getType());
    assertEquals("One of the following is true:",
        uiLibraryValidationService.hasCssOrJsDirectory().getMessage());
    assertEquals("Has a valid CSS or JS directory.",
        uiLibraryValidationService.hasCssOrJsDirectory().getBundleMessage());
  }


  @Test
  public void testIsAllIncludedScriptsFoundWhenHasCssAndJavaScript() {
    resource = context.create().resource("/ui-library");
    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    cssFolderProperties.put("include", new String[]{"file.js"});
    context.create().resource("/ui-library/js", cssFolderProperties);
    context.create().resource("/ui-library/js/file.js", fileProperties);

    final InputStream javascriptInputStream = new ByteArrayInputStream("javascript".getBytes());

    fileContentProperties.put("jcr:data", javascriptInputStream);
    fileContentProperties.put("jcr:mimeType", "application/javascript");

    context.create().resource("/ui-library/js/file.js/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertTrue(uiLibraryValidationService.isAllIncludedScriptsFound().isValid());
  }


  @Test
  public void testIsAllIncludedScriptsFoundWhenHasCss() {
    resource = context.create().resource("/ui-library");
    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertTrue(uiLibraryValidationService.isAllIncludedScriptsFound().isValid());
  }


  @Test
  public void testIsAllIncludedScriptsFoundWhenHasJavascriptJavascript() {
    resource = context.create().resource("/ui-library");
    cssFolderProperties.put("include", new String[]{"file.js"});
    context.create().resource("/ui-library/js", cssFolderProperties);
    context.create().resource("/ui-library/js/file.js", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("javascript".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "application/javascript");

    context.create().resource("/ui-library/js/file.js/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertTrue(uiLibraryValidationService.isAllIncludedScriptsFound().isValid());
  }

  @Test
  public void testIsAllIncludedScriptsFoundWhenScriptFileNotFound() {
    cssFolderProperties.put("include", new String[]{"file.css"});
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/css", cssFolderProperties);
    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();
    assertEquals(2, uiLibraryValidationService.isAllIncludedScriptsFound().getValidators().size());
    assertFalse(uiLibraryValidationService.isAllIncludedScriptsFound().isValid());
    assertEquals(ModelValidationMessageType.WARNING,
        uiLibraryValidationService.isAllIncludedScriptsFound().getType());
    assertEquals("All of the following are true:",
        uiLibraryValidationService.isAllIncludedScriptsFound().getMessage());
  }

  @Test
  public void testIsAllIncludedScriptsFoundWhenScriptCssFileIsInvalid() {
    resource = context.create().resource("/ui-library");
    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/not-css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertFalse(uiLibraryValidationService.isAllIncludedScriptsFound().isValid());
    assertEquals("All of the following are true:",
        uiLibraryValidationService.isAllIncludedScriptsFound().getMessage());
    assertEquals("All included CSS and Javascript files exist and are valid.",
        uiLibraryValidationService.isAllIncludedScriptsFound().getBundleMessage());
    assertTrue(
        uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.JAVASCRIPT).isValid());
    assertFalse(uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.CSS).isValid());
    assertEquals(ModelValidationMessageType.WARNING,
        uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.CSS).getType());
    assertEquals("All css scripts exist and are valid.",
        uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.CSS).getMessage());
  }

  @Test
  public void testIsAllIncludedScriptsFoundWhenScriptJavascriptFileIsInvalid() {
    resource = context.create().resource("/ui-library");
    cssFolderProperties.put("include", new String[]{"file.js"});
    context.create().resource("/ui-library/js", cssFolderProperties);
    context.create().resource("/ui-library/js/file.js", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("javascript".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "application/not-javascript");

    context.create().resource("/ui-library/js/file.js/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertFalse(uiLibraryValidationService.isAllIncludedScriptsFound().isValid());
    assertEquals("All of the following are true:",
        uiLibraryValidationService.isAllIncludedScriptsFound().getMessage());
    assertEquals("All included CSS and Javascript files exist and are valid.",
        uiLibraryValidationService.isAllIncludedScriptsFound().getBundleMessage());
    assertTrue(uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.CSS).isValid());
    assertFalse(
        uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.JAVASCRIPT).isValid());
    assertEquals(ModelValidationMessageType.WARNING,
        uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.JAVASCRIPT).getType());
    assertEquals("All js scripts exist and are valid.",
        uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.JAVASCRIPT).getMessage());
  }

  @Test
  public void testIsAllIncludedScriptsFoundWhenScriptTypeIsLess()
      throws InvalidResourceTypeException {
    resource = context.create().resource("/ui-library");
    cssFolderProperties.put("include", new String[]{"file.less"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.less", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/less");

    context.create().resource("/ui-library/css/file.less/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertTrue(uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.CSS).isValid());
    assertTrue(uiLibraryValidationService.isAllIncludedScriptsFound().isValid());

  }

  @Test
  public void testIsAllIncludedScriptsFoundWhenScriptTypeIsInvalid()
      throws InvalidResourceTypeException {
    resource = context.create().resource("/ui-library");
    cssFolderProperties.put("include", new String[]{"file.less"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.less", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/less");

    context.create().resource("/ui-library/css/file.less/jcr:content", fileContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertFalse(uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.LESS).isValid());
    assertTrue(uiLibraryValidationService.isAllIncludedScriptsFound(ScriptType.CSS).isValid());
  }

  @Test
  public void testIsAllDependenciesFound() {
    context.create().resource("/dependency-1", properties);

    properties.put("dependencies", "/dependency-1");
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertEquals(1, uiLibrary.getDependencies().size());
    assertTrue(uiLibraryValidationService.isAllDependenciesFound().isValid());
  }

  @Test
  public void testIsAllDependenciesFoundWhenDependencyIsMissing() {
    properties.put("dependencies", "/dependency-1");
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertFalse(uiLibraryValidationService.isAllDependenciesFound().isValid());
    assertEquals("All dependencies exist and are valid.",
        uiLibraryValidationService.isAllDependenciesFound().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        uiLibraryValidationService.isAllDependenciesFound().getType());
  }

  @Test
  public void testIsAllDependenciesFoundWhenDependencyIsInvaid() {
    properties.put("dependencies", "/dependency-1");
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    doReturn(uiLibrary).when(uiLibraryValidationService).getGenericModel();

    uiLibraryValidationService.registerBasicValidators();

    assertFalse(uiLibraryValidationService.isAllDependenciesFound().isValid());
    assertEquals("All dependencies exist and are valid.",
        uiLibraryValidationService.isAllDependenciesFound().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        uiLibraryValidationService.isAllDependenciesFound().getType());
  }

}
