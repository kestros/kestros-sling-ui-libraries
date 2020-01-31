package io.kestros.commons.uilibraries.servlets;

import io.kestros.commons.uilibraries.filetypes.ScriptType;

public abstract class BaseCssServlet extends BaseUiLibraryServlet {

  private static final long serialVersionUID = 3589556577237220081L;

  @Override
  protected ScriptType getScriptType() {
    return ScriptType.CSS;
  }
}