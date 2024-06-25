package io.kestros.commons.uilibraries.lesscss.services;

import static org.junit.Assert.*;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LessCssCompilerServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private LessCssCompilerService lessCssCompilerService;

  @Before
  public void setUp() throws Exception {
    lessCssCompilerService = new LessCssCompilerService();
  }

  @Test
  public void testGetScriptTypes() {
    assertEquals(2, lessCssCompilerService.getScriptTypes().size());
    assertEquals(".css", lessCssCompilerService.getScriptTypes().get(0).getExtension());
    assertEquals(".less", lessCssCompilerService.getScriptTypes().get(1).getExtension());
  }

  @Test
  public void testGetOutput() {
    assertEquals("", lessCssCompilerService.getOutput(""));
  }
}