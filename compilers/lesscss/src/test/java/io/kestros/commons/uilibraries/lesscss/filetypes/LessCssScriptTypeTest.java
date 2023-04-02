package io.kestros.commons.uilibraries.lesscss.filetypes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LessCssScriptTypeTest {


  @Test
  public void getName() {
    assertEquals("less", LessCssScriptType.LESS.getName());
  }


  @Test
  public void getRootResourceName() {
    assertEquals("css", LessCssScriptType.LESS.getRootResourceName());
  }

  @Test
  public void getExtension() {
    assertEquals(".less", LessCssScriptType.LESS.getExtension());
  }

  @Test
  public void getReadableExtensions() {
    assertEquals("less", LessCssScriptType.LESS.getReadableExtensions().get(1));
  }

  @Test
  public void getOutputContentType() {
    assertEquals("text/css", LessCssScriptType.LESS.getOutputContentType());
  }

  @Test
  public void getReadableContentTypes() {
    assertEquals("text/less", LessCssScriptType.LESS.getReadableContentTypes().get(1));
  }

  @Test
  public void getFileModelClass() {
    assertEquals(LessCssFile.class, LessCssScriptType.LESS.getFileModelClass());
  }
}