package io.kestros.commons.uilibraries.services.minification;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.uilibraries.exceptions.CssCompressionException;
import io.kestros.commons.uilibraries.exceptions.JavaScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true,
           service = UiLibraryMinificationService.class,
           property = "service.ranking:Integer=100")
public class BaseUiLibraryMinificationService implements UiLibraryMinificationService {

  /**
   * Compresses specified CSS string.
   *
   * @param unminifiedCss unminified CSS to compress.
   * @return Compressed CSS.
   * @throws JavaScriptCompressionException Thrown when there is an issue compressing the
   *     JavaScript.
   */
  @SuppressFBWarnings("REC_CATCH_EXCEPTION")
  private static String getMinifiedCssOutput(String unminifiedCss) throws CssCompressionException {
    try {
      StringWriter stringWriter = new StringWriter();
      CssCompressor cssCompressor = new CssCompressor(new StringReader(unminifiedCss));
      cssCompressor.compress(stringWriter, -1);
      return stringWriter.toString();
    } catch (Exception exception) {
      if (StringUtils.isNotEmpty(exception.getMessage())) {
        throw new CssCompressionException(exception.getMessage());
      } else {
        throw new CssCompressionException("Unable to minify css script.");
      }
    }
  }

  /**
   * Compresses specified javascript string.
   *
   * @param unminifiedJavascript unminified JavaScript to compress.
   * @return Compressed JavaScript.
   * @throws JavaScriptCompressionException Thrown when there is an issue compressing the
   *     JavaScript.
   */
  @SuppressFBWarnings("REC_CATCH_EXCEPTION")
  private static String getMinifiedJavaScriptOutput(String unminifiedJavascript)
      throws JavaScriptCompressionException {
    try {
      StringWriter stringWriter = new StringWriter();
      JavaScriptCompressor javaScriptCompressor = new JavaScriptCompressor(
          new StringReader(unminifiedJavascript), null);
      javaScriptCompressor.compress(stringWriter, -1, true, true, true, true);
      return stringWriter.toString();
    } catch (Exception exception) {
      throw new JavaScriptCompressionException(exception.getMessage());
    }
  }


  @Override
  public String getMinifiedOutput(String scriptOutput, ScriptType scriptType)
      throws JavaScriptCompressionException, CssCompressionException {
    if (scriptType.equals(JAVASCRIPT)) {
      return getMinifiedJavaScriptOutput(scriptOutput);
    } else {
      return getMinifiedCssOutput(scriptOutput);
    }
  }
}