package io.kestros.commons.uilibraries.lesscss.filetypes;

import static org.junit.Assert.*;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class LessCssFileTest {

  @Rule
  public SlingContext context = new SlingContext();

  private LessCssFile lessCssFile;
  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void getFileNameFromImport() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less").adaptTo(LessCssFile.class);
    assertEquals("test.less", lessCssFile.getFileNameFromImport("@import \"test.less\";"));
  }

  @Test
  public void isImportLine() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less").adaptTo(LessCssFile.class);
    assertTrue(lessCssFile.isImportLine("@import \"test.less\";"));
  }

  @Test
  public void getFileType() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less").adaptTo(LessCssFile.class);
    assertEquals(LessCssScriptType.LESS, lessCssFile.getFileType());
  }

  @Test
  @Ignore
  public void getFileContent() {

  }
}