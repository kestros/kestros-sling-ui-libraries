package io.kestros.commons.uilibraries.filetypes.css;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;

import io.kestros.commons.uilibraries.BaseScriptBuilder;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;

public class CssScriptBuilder extends BaseScriptBuilder {

  @Override
  public ScriptType getScriptType() {
    return CSS;
  }

  @Override
  public String getOutput(UiLibrary uiLibrary) {
    return getUncompiledOutput(uiLibrary);
  }

}
