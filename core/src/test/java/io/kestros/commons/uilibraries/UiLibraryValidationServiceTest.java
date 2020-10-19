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
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.validation.ModelValidationMessageType;
import io.kestros.commons.validation.models.ModelValidator;
import io.kestros.commons.validation.models.ModelValidatorBundle;
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

    properties.put("jcr:primaryType", "kes:UiLibrary");
    properties.put("jcr:title", "title");
    properties.put("jcr:description", "description");
  }


  @Test
  public void testGetModelValidators() {
    assertEquals(1, uiLibraryValidationService.getModelValidators().size());
  }

  @Test
  public void testHasCssOrJsDirectory() {
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/css");
    context.create().resource("/ui-library/js");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidator validator = uiLibraryValidationService.hasCssOrJsDirectory();
    validator.setModel(uiLibrary);
    assertTrue(validator.isValidCheck());
  }

  @Test
  public void testHasCssOrJsDirectoryWhenHasCssDirectory() {
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/css");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidator validator = uiLibraryValidationService.hasCssOrJsDirectory();
    validator.setModel(uiLibrary);
    assertTrue(validator.isValidCheck());
  }

  @Test
  public void testHasCssOrJsDirectoryWhenHasJsDirectory() {
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/js");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidator validator = uiLibraryValidationService.hasCssOrJsDirectory();
    validator.setModel(uiLibrary);
    assertTrue(validator.isValidCheck());
  }

  @Test
  public void testHasCssOrJsDirectoryWhenHasNeither() {
    resource = context.create().resource("/ui-library");
    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidator validator = uiLibraryValidationService.hasCssOrJsDirectory();
    validator.setModel(uiLibrary);

    assertFalse(validator.isValidCheck());
    assertEquals(ModelValidationMessageType.ERROR, validator.getType());
    assertEquals("One of the following is true:", validator.getDetailedMessage());
    assertEquals("Has a valid CSS or JS directory.", validator.getMessage());
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

    ModelValidator validator = uiLibraryValidationService.hasCssOrJsDirectory();
    validator.setModel(uiLibrary);
    assertTrue(validator.isValidCheck());
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

    ModelValidator validator = uiLibraryValidationService.hasCssOrJsDirectory();
    validator.setModel(uiLibrary);

    assertTrue(validator.isValidCheck());
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
    ModelValidator validator = uiLibraryValidationService.hasCssOrJsDirectory();
    validator.setModel(uiLibrary);

    assertTrue(validator.isValidCheck());
  }

  @Test
  public void testIsAllIncludedScriptsFoundWhenScriptFileNotFound() {
    cssFolderProperties.put("include", new String[]{"file.css"});
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/css", cssFolderProperties);
    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidatorBundle validator = uiLibraryValidationService.isAllIncludedScriptsFound();
    validator.setModel(uiLibrary);

    assertEquals(2, validator.getValidators().size());
    assertFalse(validator.isValidCheck());
    assertEquals(ModelValidationMessageType.WARNING, validator.getType());
    assertEquals("All of the following are true:", validator.getDetailedMessage());
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

    ModelValidatorBundle validatorBundle = uiLibraryValidationService.isAllIncludedScriptsFound();
    ModelValidator javascriptValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.JAVASCRIPT);
    ModelValidator cssValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.CSS);
    validatorBundle.setModel(uiLibrary);
    javascriptValidator.setModel(uiLibrary);
    cssValidator.setModel(uiLibrary);

    assertFalse(validatorBundle.isValidCheck());
    assertEquals("All of the following are true:", validatorBundle.getDetailedMessage());
    assertEquals("All included CSS and Javascript files exist and are valid.",
        validatorBundle.getMessage());
    assertTrue(javascriptValidator.isValidCheck());
    assertFalse(cssValidator.isValidCheck());
    assertEquals(ModelValidationMessageType.WARNING, cssValidator.getType());
    assertEquals("All css scripts exist and are valid.", cssValidator.getMessage());
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

    ModelValidatorBundle validatorBundle = uiLibraryValidationService.isAllIncludedScriptsFound();
    ModelValidator javascriptValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.JAVASCRIPT);
    ModelValidator cssValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.CSS);
    validatorBundle.setModel(uiLibrary);
    javascriptValidator.setModel(uiLibrary);
    cssValidator.setModel(uiLibrary);

    assertFalse(validatorBundle.isValidCheck());
    assertEquals("All of the following are true:", validatorBundle.getDetailedMessage());
    assertEquals("All included CSS and Javascript files exist and are valid.",
        validatorBundle.getMessage());
    assertTrue(cssValidator.isValidCheck());
    assertFalse(javascriptValidator.isValidCheck());
    assertEquals(ModelValidationMessageType.WARNING, javascriptValidator.getType());
    assertEquals("All js scripts exist and are valid.", javascriptValidator.getMessage());
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

    ModelValidatorBundle validatorBundle = uiLibraryValidationService.isAllIncludedScriptsFound();
    ModelValidator javascriptValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.JAVASCRIPT);
    ModelValidator cssValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.CSS);
    validatorBundle.setModel(uiLibrary);
    javascriptValidator.setModel(uiLibrary);
    cssValidator.setModel(uiLibrary);

    assertTrue(cssValidator.isValidCheck());
    assertTrue(validatorBundle.isValidCheck());

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

    ModelValidatorBundle validatorBundle = uiLibraryValidationService.isAllIncludedScriptsFound();
    ModelValidator lessValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.LESS);
    ModelValidator cssValidator = uiLibraryValidationService.isAllIncludedScriptsFound(
        ScriptType.CSS);
    validatorBundle.setModel(uiLibrary);
    lessValidator.setModel(uiLibrary);
    cssValidator.setModel(uiLibrary);

    assertFalse(lessValidator.isValidCheck());
    assertTrue(cssValidator.isValidCheck());
  }

  @Test
  public void testIsAllDependenciesFound() {
    context.create().resource("/dependency-1", properties);

    properties.put("dependencies", "/dependency-1");
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidator validator = uiLibraryValidationService.isAllDependenciesFound();
    validator.setModel(uiLibrary);

    assertEquals(1, uiLibrary.getDependencies().size());
    assertTrue(validator.isValidCheck());
  }

  @Test
  public void testIsAllDependenciesFoundWhenDependencyIsMissing() {
    properties.put("dependencies", "/dependency-1");
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidator validator = uiLibraryValidationService.isAllDependenciesFound();
    validator.setModel(uiLibrary);

    assertFalse(validator.isValidCheck());
    assertEquals("All dependencies exist and are valid.", validator.getMessage());
    assertEquals(ModelValidationMessageType.WARNING, validator.getType());
  }

  @Test
  public void testIsAllDependenciesFoundWhenDependencyIsInvaid() {
    properties.put("dependencies", "/dependency-1");
    resource = context.create().resource("/ui-library", properties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    ModelValidator validator = uiLibraryValidationService.isAllDependenciesFound();
    validator.setModel(uiLibrary);

    assertFalse(validator.isValidCheck());
    assertEquals("All dependencies exist and are valid.", validator.getMessage());
    assertEquals(ModelValidationMessageType.WARNING, validator.getType());
  }

}
