package io.kestros.commons.uilibraries.servlets;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;

import io.kestros.commons.uilibraries.filetypes.ScriptType;

public abstract class BaseJavaScriptServlet extends BaseUiLibraryServlet {

  private static final long serialVersionUID = 1917627832880901745L;

  @Override
  public ScriptType getScriptType() {
    return JAVASCRIPT;
  }

}