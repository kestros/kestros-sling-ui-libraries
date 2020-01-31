package io.kestros.commons.uilibraries.filetypes.less;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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

  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> importerContentProperties = new HashMap<>();
  private Map<String, Object> importedContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros.commons.uilibraries");
  }

  @Test
  public void testValidate() throws Exception {
    resource = context.create().resource("/my-less.less");

    lessFile = resource.adaptTo(LessFile.class);

    assertEquals(0, lessFile.getErrorMessages().size());
    assertEquals(0, lessFile.getWarningMessages().size());
    assertEquals(0, lessFile.getInfoMessages().size());
  }

  @Test
  public void testGetOutputWithImportsResolved() throws Exception {
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

    assertEquals(ScriptType.LESS, lessFile.getFileType());
    assertEquals("body{\n" + "div{\n" + "\tcolor: red;\n" + "}\n" + "}", lessFile.getOutput());
  }

  @Test
  public void testGetOutputWhenIOException() throws Exception {
    fileProperties.put("jcr:primaryType", "nt:file");

    resource = context.create().resource("/my-less.less", fileProperties);

    lessFile = resource.adaptTo(LessFile.class);

    lessFile = spy(lessFile);
    final BufferedReader mockBufferedReader = mock(BufferedReader.class);

    when(mockBufferedReader.readLine()).thenThrow(new IOException());

    doReturn(mockBufferedReader).when(lessFile).getBufferedReader();

    assertEquals("", lessFile.getOutput());
  }

  @Test
  public void testGetOutputWithImportsResolvedWhenFileNotFound() throws Exception {
    fileProperties.put("jcr:primaryType", "nt:file");

    final InputStream importerInputStream = new ByteArrayInputStream(
        "body{\n\t@import \"import-1.less\";\n}".getBytes());

    importerContentProperties.put("jcr:data", importerInputStream);

    resource = context.create().resource("/my-less.less", fileProperties);
    context.create().resource("/my-less.less/jcr:content", importerContentProperties);

    lessFile = resource.adaptTo(LessFile.class);

    lessFile.getOutput();
    assertEquals("body{\n" + "\t@import \"import-1.less\";\n" + "}", lessFile.getOutput());
  }

  @Test
  public void testGetOutputWithImportsResolvedWhenInvalidResourceType() throws Exception {
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
    lessFile.validate();

    assertEquals("body{\n" + "\t@import \"import-1.png\";\n" + "}", lessFile.getOutput());
  }


  @Test
  public void testGetOutputWithImportsResolvedWhenNestedImports() throws Exception {
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
        lessFile.getOutput());
  }

  @Test
  public void testGetFileNameFromImport() throws Exception {
    assertEquals("my-file.less", LessFile.getFileNameFromImport("@import " + "\"my-file.less\";"));
  }

  @Test
  public void testGetFileNameFromImportWhenInvalidImportSyntax() throws Exception {
    assertEquals("", LessFile.getFileNameFromImport("import \"my-file" + ".less\";"));
  }

  @Test
  public void testIsImportLine() throws Exception {
    assertTrue(LessFile.isImportLine("@import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenMissingFirstQuote() throws Exception {
    assertFalse(LessFile.isImportLine("@import test.less\";"));
  }

  @Test
  public void testIsImportLineWhenMissingSecondQuote() throws Exception {
    assertFalse(LessFile.isImportLine("@import \"test.less;"));
  }

  @Test
  public void testIsImportLineWhenInvalidImportSyntax() throws Exception {
    assertFalse(LessFile.isImportLine("import \"test.less\";"));
    assertFalse(LessFile.isImportLine("@test-import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenHasSpaces() throws Exception {
    assertTrue(LessFile.isImportLine("    @import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenHasTabs() throws Exception {
    assertTrue(LessFile.isImportLine("\t\t@import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenHasNewLine() throws Exception {
    assertTrue(LessFile.isImportLine("\n\n@import \"test.less\";"));
  }

  @Test
  public void testIsImportLineWhenComment() throws Exception {
    assertFalse(LessFile.isImportLine("// @import \"test.less\";"));
  }


}