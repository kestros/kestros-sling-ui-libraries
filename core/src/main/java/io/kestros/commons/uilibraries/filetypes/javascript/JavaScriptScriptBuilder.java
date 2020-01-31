package io.kestros.commons.uilibraries.filetypes.javascript;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;

import io.kestros.commons.uilibraries.BaseScriptBuilder;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;

public class JavaScriptScriptBuilder extends BaseScriptBuilder {

  @Override
  public ScriptType getScriptType() {
    return JAVASCRIPT;
  }

  @Override
  public String getOutput(final UiLibrary uiLibrary) {
    return getUncompiledOutput(uiLibrary);
  }

}
