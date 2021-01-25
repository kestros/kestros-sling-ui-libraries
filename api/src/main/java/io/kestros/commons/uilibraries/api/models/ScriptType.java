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

import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Filetypes that can be compiled into either CSS or JavaScript (including .css and .js).
 */
public interface ScriptType extends FileType {

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

}