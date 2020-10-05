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

package io.kestros.commons.uilibraries.filetypes.less;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.filetypes.css.CssFile;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LessFileTest {

  @Rule
  public SlingContext context = new SlingContext();

  private LessFile lessFile;

  private Resource resource;

  private Resource jcrContentResource;

  private final Map<String, Object> fileProperties = new HashMap<>();
  private final Map<String, Object> importerContentProperties = new HashMap<>();
  private final Map<String, Object> importedContentProperties = new HashMap<>();

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros.commons.uilibraries");
  }

  @Test
  public void testValidate() {
    resource = context.create().resource("/my-less.less");

    lessFile = resource.adaptTo(LessFile.class);

    assertEquals(ScriptType.LESS, lessFile.getFileType());
  }

  @Test
  public void testGetOutputWithImportsResolved() {
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.less\";\n}".getBytes());
    final InputStream importedInputStream = new ByteArrayInputStream("div{\n\tcolor: red;\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);
    importedContentProperties.put("jcr:data", importedInputStream);
    importedContentProperties.put("jcr:mimeType", "text/less");

    resource = context.create().resource("/my-less.less", fileProperties);
    context.create().resource("/my-less.less/jcr:content", importerContentProperties);

    context.create().resource("/import-1.less", fileProperties);
    context.create().resource("/import-1.less/jcr:content", importedContentProperties);

    lessFile = resource.adaptTo(LessFile.class);

    assertEquals(ScriptType.LESS, Objects.requireNonNull(lessFile).getFileType());
    assertEquals("body{\n" + "div{\n" + "\tcolor: red;\n" + "}\n" + "}", lessFile.getFileContent());
  }

  @Test
  public void testGetOutputWhenIOException() throws Exception {
    fileProperties.put("jcr:primaryType", "nt:file");

    resource = context.create().resource("/my-less.less", fileProperties);

    lessFile = resource.adaptTo(LessFile.class);

    lessFile = spy(Objects.requireNonNull(lessFile));
    final BufferedReader mockBufferedReader = mock(BufferedReader.class);

    when(mockBufferedReader.readLine()).thenThrow(new IOException());

    doReturn(mockBufferedReader).when(lessFile).getBufferedReader();

    assertEquals("", lessFile.getFileContent());
  }

  @Test
  public void testGetOutputWithImportsResolvedWhenFileNotFound() {
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.less\";\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);

    resource = context.create().resource("/my-less.less", fileProperties);
    context.create().resource("/my-less.less/jcr:content", importerContentProperties);

    lessFile = resource.adaptTo(LessFile.class);

    assertEquals("body{\n" + "\t@import \"import-1.less\";\n" + "}", Objects.requireNonNull(
        lessFile).getFileContent());
  }

  @Test
  public void testGetOutputWithImportsResolvedWhenInvalidResourceType() {
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.png\";\n}".getBytes());
    final InputStream importedInputStream = new ByteArrayInputStream("div{\n\tcolor: red;\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);
    importedContentProperties.put("jcr:data", importedInputStream);

    resource = context.create().resource("/my-less.less", fileProperties);
    context.create().resource("/my-less.less/jcr:content", importerContentProperties);

    context.create().resource("/import-1.png", fileProperties);
    context.create().resource("/import-1.png/jcr:content", importedContentProperties);

    lessFile = resource.adaptTo(LessFile.class);

    assertEquals("body{\n" + "\t@import \"import-1.png\";\n" + "}", lessFile.getFileContent());
  }


  @Test
  public void testGetOutputWithImportsResolvedWhenNestedImports() {
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.less\";\n}".getBytes());
    final InputStream importedInputStream = new ByteArrayInputStream(
        "div{\n\t@import \"import-2.less\";\n}".getBytes());

    final InputStream secondImportedInputStream = new ByteArrayInputStream(
        "div{\n\tcolor: red;\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);
    importedContentProperties.put("jcr:data", importedInputStream);

    resource = context.create().resource("/my-less.less", fileProperties);
    importerContentProperties.put("jcr:mimeType", "text/less");
    context.create().resource("/my-less.less/jcr:content", importerContentProperties);

    context.create().resource("/import-1.less", fileProperties);
    importedContentProperties.put("jcr:mimeType", "text/less");
    context.create().resource("/import-1.less/jcr:content", importedContentProperties);

    importedContentProperties.put("jcr:data", secondImportedInputStream);

    context.create().resource("/import-2.less", fileProperties);
    context.create().resource("/import-2.less/jcr:content", importedContentProperties);

    lessFile = resource.adaptTo(LessFile.class);

    assertEquals("body{\n" + "div{\n" + "div{\n" + "\tcolor: red;\n" + "}\n" + "}\n" + "}",
        Objects.requireNonNull(lessFile).getFileContent());
  }

  @Test
  public void testGetFileNameFromImport() {
    assertEquals("my-file.less", LessFile.getFileNameFromImport("@import " + "\"my-file.less\";"));
  }

  @Test
  public void testGetFileNameFromImportWhenInvalidImportSyntax() {
    assertEquals("", LessFile.getFileNameFromImport("import \"my-file" + ".less\";"));
  }

  @Test
  public void testIsImportLine() {
    assertTrue(LessFile.isImportLine("@import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenMissingFirstQuote() {
    assertFalse(LessFile.isImportLine("@import test.less\";"));
  }

  @Test
  public void testIsImportLineWhenMissingSecondQuote() {
    assertFalse(LessFile.isImportLine("@import \"test.less;"));
  }

  @Test
  public void testIsImportLineWhenInvalidImportSyntax() {
    assertFalse(LessFile.isImportLine("import \"test.less\";"));
    assertFalse(LessFile.isImportLine("@test-import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenHasSpaces() {
    assertTrue(LessFile.isImportLine("    @import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenHasTabs() {
    assertTrue(LessFile.isImportLine("\t\t@import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenHasNewLine() {
    assertTrue(LessFile.isImportLine("\n\n@import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenComment() {
    assertFalse(LessFile.isImportLine("// @import \"test.less\";"));
  }


}