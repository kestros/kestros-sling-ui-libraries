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

package io.kestros.commons.uilibraries.exceptions;

import io.kestros.commons.uilibraries.filetypes.ScriptType;

/**
 * Generic Exception thrown when CSS or JavaScript output failed to minify/compress.
 */
public class ScriptCompressionException extends Exception {

  /**
   * Generic Exception thrown when CSS or JavaScript output failed to minify/compress.
   *
   * @param message Cause message.
   */
  public ScriptCompressionException(final String message) {
    super(message);
  }

  /**
   * Generic Exception thrown when CSS or JavaScript output failed to minify/compress.
   *
   * @param resourcePath Absolute path to the UiLibrary that could not be compressed.
   * @param scriptType CSS or JavaScript Type.
   * @param message Cause message.
   */
  public ScriptCompressionException(final String resourcePath, final ScriptType scriptType,
      final String message) {
    super(String.format("Unable to compress %s script for '%s'. %s.", scriptType.getName(),
        resourcePath, message));
  }

}
