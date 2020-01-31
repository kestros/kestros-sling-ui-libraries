package io.kestros.commons.uilibraries.filetypes.javascript;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static org.junit.Assert.*;

import org.junit.Test;

public class JavaScriptFileTest {

  @Test
  public void testGetScriptType() {
    assertEquals(JAVASCRIPT, new JavaScriptFile().getFileType());
  }
}