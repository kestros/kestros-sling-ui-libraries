package io.kestros.commons.uilibraries.filetypes.less;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;
import static org.junit.Assert.*;

import org.junit.Test;

public class LessScriptBuilderTest {

  @Test
  public void testGetScriptType() throws Exception {
    assertEquals(LESS, new LessScriptBuilder().getScriptType());
  }
}