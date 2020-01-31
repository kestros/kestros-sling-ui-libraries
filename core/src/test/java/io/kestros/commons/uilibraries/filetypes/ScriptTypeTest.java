package io.kestros.commons.uilibraries.filetypes;

import static org.junit.Assert.assertEquals;

import io.kestros.commons.uilibraries.filetypes.css.CssFile;
import io.kestros.commons.uilibraries.filetypes.css.CssScriptBuilder;
import io.kestros.commons.uilibraries.filetypes.javascript.JavaScriptFile;
import io.kestros.commons.uilibraries.filetypes.javascript.JavaScriptScriptBuilder;
import io.kestros.commons.uilibraries.filetypes.less.LessFile;
import io.kestros.commons.uilibraries.filetypes.less.LessScriptBuilder;
import org.junit.Test;

public class ScriptTypeTest {

  @Test
  public void testGetName() {
    assertEquals("css", ScriptType.CSS.getName());
    assertEquals("js", ScriptType.JAVASCRIPT.getName());
    assertEquals("less", ScriptType.LESS.getName());


  }

  @Test
  public void testGetRootResourceName() {
    assertEquals("css", ScriptType.CSS.getRootResourceName());
    assertEquals("js", ScriptType.JAVASCRIPT.getRootResourceName());
    assertEquals("css", ScriptType.LESS.getRootResourceName());
  }

  @Test
  public void testGetExtension() {
    assertEquals(".css", ScriptType.CSS.getExtension());
    assertEquals(".js", ScriptType.JAVASCRIPT.getExtension());
    assertEquals(".less", ScriptType.LESS.getExtension());
  }

  @Test
  public void testGetOutputContentType() {
    assertEquals("text/css", ScriptType.CSS.getOutputContentType());
    assertEquals("application/javascript", ScriptType.JAVASCRIPT.getOutputContentType());
    assertEquals("text/css", ScriptType.LESS.getOutputContentType());
  }

  @Test
  public void testGetReadableContentTypes() {
    assertEquals(1, ScriptType.CSS.getReadableContentTypes().size());
    assertEquals("text/css", ScriptType.CSS.getReadableContentTypes().get(0));

    assertEquals(1, ScriptType.JAVASCRIPT.getReadableContentTypes().size());
    assertEquals("application/javascript", ScriptType.JAVASCRIPT.getReadableContentTypes().get(0));

    assertEquals("text/css", ScriptType.LESS.getReadableContentTypes().get(0));
    assertEquals("text/less", ScriptType.LESS.getReadableContentTypes().get(1));
  }

  @Test
  public void testGetScriptBuilder() {
    assertEquals(CssScriptBuilder.class, ScriptType.CSS.getScriptBuilder().getClass());
    assertEquals(JavaScriptScriptBuilder.class,
        ScriptType.JAVASCRIPT.getScriptBuilder().getClass());
    assertEquals(LessScriptBuilder.class, ScriptType.LESS.getScriptBuilder().getClass());
  }

  @Test
  public void testGetFileModelClass() {
    assertEquals(CssFile.class, ScriptType.CSS.getFileModelClass());
    assertEquals(JavaScriptFile.class, ScriptType.JAVASCRIPT.getFileModelClass());
    assertEquals(LessFile.class, ScriptType.LESS.getFileModelClass());
  }
}