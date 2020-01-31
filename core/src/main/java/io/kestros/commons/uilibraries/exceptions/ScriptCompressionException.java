package io.kestros.commons.uilibraries.exceptions;

import io.kestros.commons.uilibraries.filetypes.ScriptType;

public class ScriptCompressionException extends Exception {

  public ScriptCompressionException(final String message) {
    super(message);
  }

  public ScriptCompressionException(
      final String resourcePath, final ScriptType scriptType, final String message) {
    super(String.format("Unable to compress %s script for '%s'. %s.", scriptType.getName(),
        resourcePath, message));
  }

}
