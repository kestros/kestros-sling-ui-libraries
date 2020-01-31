package io.kestros.commons.uilibraries.services.minification;

import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;

/**
 * Service for determining how CSS and JS scripts will be compressed/minified.
 */
public interface UiLibraryMinificationService {

  /**
   * Compressed the specified string as a ScriptType.
   *
   * @param scriptOutput string to compress.
   * @param scriptType ScriptType to compress the String as.
   * @return The specified String, compressed/minified.
   * @throws ScriptCompressionException Passed script failed to compress.
   */
  String getMinifiedOutput(String scriptOutput, ScriptType scriptType)
      throws ScriptCompressionException;

}