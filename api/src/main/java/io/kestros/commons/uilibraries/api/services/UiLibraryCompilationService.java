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

package io.kestros.commons.uilibraries.api.services;

import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.models.UiLibrary;
import java.util.List;

/**
 * Compiles CSS and JS for {@link UiLibrary}.
 */
public interface UiLibraryCompilationService extends ManagedService {

  /**
   * All registered script types.
   *
   * @return All registered script types.
   */
  List<ScriptType> getAllRegisteredScriptTypes();

  /**
   * All registered CSS script types.
   *
   * @return All registered CSS script types.
   */
  List<ScriptType> getRegisteredCssScriptTypes();

  /**
   * All registered JavaScript script types.
   *
   * @return All registered JavaScript script types.
   */
  List<ScriptType> getRegisteredJavaScriptScriptTypes();

  /**
   * All registered CSS compilers.
   *
   * @return All registered CSS compilers.
   */
  List<CssScriptTypeCompilerService> getCssCompilers();

  /**
   * All registered JavaScript compilers.
   *
   * @return All registered JavaScript compilers.
   */
  List<JavaScriptScriptTypeCompilerService> getJavaScriptCompilers();

  /**
   * Registered compiler services.
   *
   * @return Registered compiler services.
   */
  List<ScriptTypeCompiler> getCompilers();

  /**
   * Retrieves a specified ScriptType compiler.
   *
   * @param scriptTypes ScriptType to get the compiler for.
   * @param registeredCompilers Compilers to search from.
   * @param <T> Extends ScriptTypeCompiler.
   * @return Specified ScriptType compiler.
   * @throws NoMatchingCompilerException No compiler could be found for the specified {@link
   *     ScriptType}.
   */
  <T extends ScriptTypeCompiler> ScriptTypeCompiler getCompiler(
      List<ScriptType> scriptTypes, List<T> registeredCompilers)
      throws NoMatchingCompilerException;

  /**
   * Returns the uncached ScriptType output.
   *
   * @param library UI Library to retrieve output for.
   * @param scriptType ScriptType to retrieve.
   * @param minify Whether to minify the output.
   * @return The uncached ScriptType output.
   * @throws NoMatchingCompilerException No compiler could be found for the specified {@link
   *     ScriptType}.
   * @throws InvalidResourceTypeException Thrown when a referenced dependency could not be
   *     adapted to UiLibrary.
   */
  String getUiLibraryOutput(FrontendLibrary library, ScriptType scriptType, Boolean minify)
      throws InvalidResourceTypeException, NoMatchingCompilerException;

}
