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

import java.util.List;

/**
 * Baseline interface for frontend libraries, which can render CSS and JavaScript.
 */
public interface FrontendLibrary {

  /**
   * Library title.
   *
   * @return Library title.
   */
  String getTitle();

  /**
   * Library description.
   *
   * @return Library description.
   */
  String getDescription();

  /**
   * Path to library resource.
   *
   * @return Path to library resource.
   */
  String getPath();

  /**
   * Path to CSS endpoint.
   *
   * @return Path to CSS endpoint.
   */
  String getCssPath();

  /**
   * Path to JavaScript endpoint.
   *
   * @return Path to JavaScript endpoint.
   */
  String getJsPath();

  /**
   * Included script files.
   *
   * @param scriptTypes Script types to retrieve.
   * @param folderName Folder to retrieve scripts from.
   * @param <T> extends {@link ScriptFileInterface}.
   * @return Included script files.
   */
  <T extends ScriptFileInterface> List<T> getScriptFiles(
      final List<ScriptTypeInterface> scriptTypes, String folderName);
}
