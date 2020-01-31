package io.kestros.commons.uilibraries.services.minification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.kestros.commons.uilibraries.exceptions.CssCompressionException;
import io.kestros.commons.uilibraries.exceptions.JavaScriptCompressionException;
import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class BaseUiLibraryMinificationServiceTest {

  private BaseUiLibraryMinificationService minificationService;

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    minificationService = new BaseUiLibraryMinificationService();
  }

  @Test
  public void testGetMinifiedOutputWhenCss() throws ScriptCompressionException {
    assertEquals("body{color:red}",
        minificationService.getMinifiedOutput("\nbody {\ncolor: red;\n}", ScriptType.CSS));
  }

  @Test
  public void testGetMinifiedOutputWhenCssAndCssCompressionException() {
    try {
      minificationService.getMinifiedOutput(null, ScriptType.CSS);
    } catch (JavaScriptCompressionException e) {
    } catch (CssCompressionException e) {
      exception = e;
    }
    assertEquals("Unable to minify css script.", exception.getMessage());
  }

  @Test
  public void testGetMinifiedOutputWhenJavascript() throws ScriptCompressionException {
    assertEquals("console.log(\"test\");console.log(\"test\");",
        minificationService.getMinifiedOutput("console.log('test');\n\nconsole.log('test');\n\n",
            ScriptType.JAVASCRIPT));
  }

  @Test
  public void testGetMinifiedOutputWhenJavascriptAndJavascriptCompressionException() {
    try {
      minificationService.getMinifiedOutput("", ScriptType.JAVASCRIPT);
    } catch (JavaScriptCompressionException e) {
      exception = e;
    } catch (CssCompressionException e) {
    }
    assertTrue(StringUtils.isNotEmpty(exception.getMessage()));
  }
}