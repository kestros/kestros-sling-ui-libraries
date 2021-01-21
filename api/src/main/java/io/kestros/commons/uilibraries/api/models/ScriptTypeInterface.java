package io.kestros.commons.uilibraries.api.models;

import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import java.util.List;
import javax.annotation.Nonnull;

public interface ScriptTypeInterface extends FileType {

  /**
   * Resource folder name where scripts of this type should live.
   *
   * @return Resource folder name where scripts of this type should live.
   */
  @Nonnull
  String getRootResourceName();

  /**
   * Extensions that can be interpreted by this FileType and its associated Model.
   *
   * @return Extensions that can be interpreted by this FileType and its associated Model.
   */
  List<String> getReadableExtensions();

  /**
   * {@link BaseScriptBuilder} to associate with a ScriptType.
   *
   * @param <T> Extends {@link BaseScriptBuilder}
   * @return {@link BaseScriptBuilder} to associate with a ScriptType
   */
  @Nonnull
  <T extends ScriptBuilderInterface> T getScriptBuilder();

}