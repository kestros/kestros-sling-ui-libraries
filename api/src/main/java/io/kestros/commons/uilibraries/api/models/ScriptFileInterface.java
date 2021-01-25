/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.kestros.commons.uilibraries.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import java.io.IOException;

/**
 * Interface for CSS or JavaScript  files(or file types that can compile into them).
 */
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
