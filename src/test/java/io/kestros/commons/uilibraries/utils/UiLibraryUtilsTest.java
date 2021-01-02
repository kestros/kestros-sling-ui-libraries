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

package io.kestros.commons.uilibraries.utils;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;
import static org.junit.Assert.assertEquals;

import io.kestros.commons.uilibraries.UiLibrary;
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

public class UiLibraryUtilsTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private UiLibrary uiLibrary;

  private Resource resource;

  private final Map<String, Object> properties = new HashMap<>();
  private final Map<String, Object> uiLibraryProperties = new HashMap<>();
  private final Map<String, Object> cssRootProperties = new HashMap<>();
  private final Map<String, Object> fileProperties = new HashMap<>();
  private final Map<String, Object> importerContentProperties = new HashMap<>();
  private final Map<String, Object> importedContentProperties = new HashMap<>();

  @Before
  public void setup() {
    context.addModelsForPackage("io.kestros.commons.uilibraries");
  }

  @Test
  public void testGetCompiledUiLibraryOutputWhenLess() {
    uiLibraryProperties.put("jcr:primaryType", "kes:UiLibrary");
    cssRootProperties.put("include", "my-less.less");
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.less\";\n}".getBytes());
    final InputStream importedInputStream = new ByteArrayInputStream(
        "div{\n\t@import \"import-2.less\";\n}".getBytes());

    final InputStream secondImportedInputStream = new ByteArrayInputStream(
        "div{\n\tcolor: red;\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);
    importedContentProperties.put("jcr:data", importedInputStream);
    importerContentProperties.put("jcr:mimeType", "text/less");
    importedContentProperties.put("jcr:mimeType", "text/less");

    resource = context.create().resource("/ui-library", uiLibraryProperties);
    context.create().resource("/ui-library/css", cssRootProperties);

    context.create().resource("/ui-library/css/my-less.less", fileProperties);
    context.create().resource("/ui-library/css/my-less.less/jcr:content",
        importerContentProperties);

    context.create().resource("/ui-library/css/import-1.less", fileProperties);
    context.create().resource("/ui-library/css/import-1.less/jcr:content",
        importedContentProperties);

    importedContentProperties.put("jcr:data", secondImportedInputStream);

    context.create().resource("/ui-library/css/import-2.less", fileProperties);
    context.create().resource("/ui-library/css/import-2.less/jcr:content",
        importedContentProperties);

    final UiLibrary uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("body div div {\n" + "  color: red;\n" + "}\n",
        UiLibraryUtils.getScriptOutput(CSS, uiLibrary, true));
  }

  @Test
  public void testGetCompiledUiLibraryOutputWhenLessWhenScripFilesHaveMultiplePeriods() {
    uiLibraryProperties.put("jcr:primaryType", "kes:UiLibrary");
    cssRootProperties.put("include", "my-less.test.less");
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.test.less\";\n}".getBytes());
    final InputStream importedInputStream = new ByteArrayInputStream(
        "div{\n\t@import \"import-2.test.less\";\n}".getBytes());

    final InputStream secondImportedInputStream = new ByteArrayInputStream(
        "div{\n\tcolor: red;\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);
    importedContentProperties.put("jcr:data", importedInputStream);
    importerContentProperties.put("jcr:mimeType", "text/less");
    importedContentProperties.put("jcr:mimeType", "text/less");

    resource = context.create().resource("/ui-library", uiLibraryProperties);
    context.create().resource("/ui-library/css", cssRootProperties);

    context.create().resource("/ui-library/css/my-less.test.less", fileProperties);
    context.create().resource("/ui-library/css/my-less.test.less/jcr:content",
        importerContentProperties);

    context.create().resource("/ui-library/css/import-1.test.less", fileProperties);
    context.create().resource("/ui-library/css/import-1.test.less/jcr:content",
        importedContentProperties);

    importedContentProperties.put("jcr:data", secondImportedInputStream);

    context.create().resource("/ui-library/css/import-2.test.less", fileProperties);
    context.create().resource("/ui-library/css/import-2.test.less/jcr:content",
        importedContentProperties);

    final UiLibrary uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("body div div {\n" + "  color: red;\n" + "}\n",
        UiLibraryUtils.getScriptOutput(CSS, uiLibrary, true));
  }

  @Test
  public void testGetCompiledUiLibraryOutputWhenCss() {
    uiLibraryProperties.put("jcr:primaryType", "kes:UiLibrary");
    cssRootProperties.put("include", "my-css.css");
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream inputStream = new ByteArrayInputStream(
        "body{\n\tcolor: red; \n// @import \"123.less\";\n}".getBytes());

    importerContentProperties.put("jcr:data", inputStream);
    importerContentProperties.put("jcr:mimeType", "text/css");

    resource = context.create().resource("/ui-library", uiLibraryProperties);
    context.create().resource("/ui-library/css", cssRootProperties);

    context.create().resource("/ui-library/css/my-css.css", fileProperties);
    context.create().resource("/ui-library/css/my-css.css/jcr:content", importerContentProperties);

    final UiLibrary uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("body{\n" + "\tcolor: red; \n" + "// @import \"123.less\";\n" + "}\n",
        UiLibraryUtils.getScriptOutput(CSS, uiLibrary, true));
    assertEquals("body{\n" + "\tcolor: red; \n" + "// @import \"123.less\";\n" + "}\n",
        UiLibraryUtils.getScriptOutput(LESS, uiLibrary, true));
  }

  @Test
  public void testGetCompiledUiLibraryOutputWhenCssWhenWrongMimeType() {
    uiLibraryProperties.put("jcr:primaryType", "kes:UiLibrary");
    cssRootProperties.put("include", "my-css.css");
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream inputStream = new ByteArrayInputStream(
        "body{\n\tcolor: red; \n// @import \"123.less\";\n}".getBytes());

    importerContentProperties.put("jcr:data", inputStream);
    importerContentProperties.put("jcr:mimeType", "text/less");

    resource = context.create().resource("/ui-library", uiLibraryProperties);
    context.create().resource("/ui-library/css", cssRootProperties);

    context.create().resource("/ui-library/css/my-css.css", fileProperties);
    context.create().resource("/ui-library/css/my-css.css/jcr:content", importerContentProperties);

    final UiLibrary uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("", UiLibraryUtils.getScriptOutput(CSS, uiLibrary, true));
  }

  @Test
  public void testGetCompiledUiLibraryOutputWhenJavaScript() {
    uiLibraryProperties.put("jcr:primaryType", "kes:UiLibrary");
    cssRootProperties.put("include", "my-javascript.js");
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream inputStream = new ByteArrayInputStream("console.log(\"test\");".getBytes());

    importerContentProperties.put("jcr:data", inputStream);
    importerContentProperties.put("jcr:mimeType", "application/javascript");
    importedContentProperties.put("jcr:mimeType", "application/javascript");

    resource = context.create().resource("/ui-library", uiLibraryProperties);
    context.create().resource("/ui-library/js", cssRootProperties);

    context.create().resource("/ui-library/js/my-javascript.js", fileProperties);
    context.create().resource("/ui-library/js/my-javascript.js/jcr:content",
        importerContentProperties);

    final UiLibrary uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("console.log(\"test\");\n",
        UiLibraryUtils.getScriptOutput(ScriptType.JAVASCRIPT, uiLibrary, true));
  }

  @Test
  public void testGetUncompiledUiLibraryOutputWhenLess() {
    uiLibraryProperties.put("jcr:primaryType", "kes:UiLibrary");
    cssRootProperties.put("include", "my-less.less");
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.less\";\n}".getBytes());
    final InputStream importedInputStream = new ByteArrayInputStream(
        "div{\n\t@import \"import-2.less\";\n}".getBytes());

    final InputStream secondImportedInputStream = new ByteArrayInputStream(
        "div{\n\tcolor: red;\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);
    importedContentProperties.put("jcr:data", importedInputStream);
    importerContentProperties.put("jcr:mimeType", "text/less");
    importedContentProperties.put("jcr:mimeType", "text/less");

    resource = context.create().resource("/ui-library", uiLibraryProperties);
    context.create().resource("/ui-library/css", cssRootProperties);

    context.create().resource("/ui-library/css/my-less.less", fileProperties);
    context.create().resource("/ui-library/css/my-less.less/jcr:content",
        importerContentProperties);

    context.create().resource("/ui-library/css/import-1.less", fileProperties);
    context.create().resource("/ui-library/css/import-1.less/jcr:content",
        importedContentProperties);

    importedContentProperties.put("jcr:data", secondImportedInputStream);

    context.create().resource("/ui-library/css/import-2.less", fileProperties);
    context.create().resource("/ui-library/css/import-2.less/jcr:content",
        importedContentProperties);

    final UiLibrary uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals(31, UiLibraryUtils.getScriptOutput(LESS, uiLibrary, true).length());
    assertEquals("body{\n" + "div{\n" + "div{\n" + "\tcolor: red;\n" + "}\n" + "}\n" + "}",
        UiLibraryUtils.getScriptOutput(LESS, uiLibrary, false));
  }


}