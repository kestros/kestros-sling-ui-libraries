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

/**
 * Service which compresses CSS and JavaScript Strings for
 * {@link io.kestros.commons.uilibraries.UiLibrary}
 * output.
 */
@Component(immediate = true,
           service = UiLibraryMinificationService.class,
           property = "service.ranking:Integer=100")
public class BaseUiLibraryMinificationService implements UiLibraryMinificationService {

  /**
   * Compresses specified CSS string.
   *
   * @param unminifiedCss unminified CSS to compress.
   * @return Compressed CSS.
   * @throws CssCompressionException Thrown when there is an issue compressing the cssScript.
   */
  @SuppressFBWarnings("REC_CATCH_EXCEPTION")
  private static String getMinifiedCssOutput(final String unminifiedCss)
      throws CssCompressionException {
    try {
      final StringWriter stringWriter = new StringWriter();
      final CssCompressor cssCompressor = new CssCompressor(new StringReader(unminifiedCss));
      cssCompressor.compress(stringWriter, -1);
      return stringWriter.toString();
    } catch (final Exception exception) {
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
  private static String getMinifiedJavaScriptOutput(final String unminifiedJavascript)
      throws JavaScriptCompressionException {
    try {
      final StringWriter stringWriter = new StringWriter();
      final JavaScriptCompressor javaScriptCompressor = new JavaScriptCompressor(
          new StringReader(unminifiedJavascript), null);
      javaScriptCompressor.compress(stringWriter, -1, true, true, true, true);
      return stringWriter.toString();
    } catch (final Exception exception) {
      throw new JavaScriptCompressionException(exception.getMessage());
    }
  }


  @Override
  public String getMinifiedOutput(final String scriptOutput, final ScriptType scriptType)
      throws JavaScriptCompressionException, CssCompressionException {
    if (scriptType.equals(JAVASCRIPT)) {
      return getMinifiedJavaScriptOutput(scriptOutput);
    } else {
      return getMinifiedCssOutput(scriptOutput);
    }
  }
}