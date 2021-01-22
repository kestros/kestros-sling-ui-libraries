package io.kestros.commons.uilibraries.api.models;

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.util.List;

public interface UiLibraryInterface {

  String getCssPath();

  String getJsPath();

  List<UiLibraryInterface> getDependencies();

  String getOutput(final ScriptTypeInterface scriptType, final boolean minify)
      throws InvalidResourceTypeException;

  List<ScriptTypeInterface> getSupportedScriptTypes();

  <T extends ScriptFileInterface> List<T> getScriptFiles(final ScriptTypeInterface scriptType);

}