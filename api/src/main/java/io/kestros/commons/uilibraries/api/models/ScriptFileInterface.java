package io.kestros.commons.uilibraries.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import java.io.IOException;

public interface ScriptFileInterface {

  /**
   * File name.
   *
   * @return File name.
   */
  String getName();

  /**
   * {@link FileType} associated to the BaseFile implementation model. Used for checking resource
   * validity when adapting Resources to this File model.
   *
   * @return {@link FileType} associated to the BaseFile implementation model.
   */
  FileType getFileType();

  /**
   * Extension of the current file resource.
   *
   * @return Extension of the current file resource.
   */
  String getExtension();

  /**
   * jcr:mimeType property value.
   *
   * @return jcr:mimeType property value.
   */
  String getMimeType();

  /**
   * Size of the current File.
   *
   * @return Size of the current File.
   */
  String getFileSize();

  /**
   * Content of the current File, as a String.
   *
   * @return Content of the current File, as a String.
   * @throws IOException Thrown when there is an error reading contents of the File.
   */
  @JsonIgnore
  String getFileContent() throws IOException;

}
