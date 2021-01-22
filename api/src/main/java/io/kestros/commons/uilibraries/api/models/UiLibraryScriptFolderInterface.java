package io.kestros.commons.uilibraries.api.models;

import java.util.List;
import javax.annotation.Nonnull;

public interface UiLibraryScriptFolderInterface {

  /**
   * Filenames included script files.
   *
   * @return Filenames included script files.
   */
  @Nonnull
  List<String> getIncludedScriptNames();

  /**
   * List of all ScriptFile resources for the specified ScriptType.
   *
   * @param scriptType ScriptType to retrieve.
   * @param <T> extends ScriptType
   * @return List of all ScriptFile resources for the specified ScriptType.
   */
  <T extends ScriptFileInterface> List<T> getScriptFiles(final ScriptTypeInterface scriptType);

}
