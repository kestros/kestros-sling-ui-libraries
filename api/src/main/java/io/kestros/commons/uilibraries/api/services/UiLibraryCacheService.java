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

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.models.ScriptType;

/**
 * Service for handling UiLibrary caches.
 */
public interface UiLibraryCacheService extends CacheService {

  /**
   * Retrieves the cached output for a specified UiLibrary.
   *
   * @param libraryPath UiLibrary to retrieve cached script for.
   * @param scriptType ScriptType (CSS or JS) to retrieve.
   * @param minified Whether to retrieved the minified version.
   * @return The cached output for a specified UiLibrary.
   * @throws CacheRetrievalException Cached output could not be retrieved.
   */
  String getCachedOutput(String libraryPath, ScriptType scriptType, boolean minified)
      throws CacheRetrievalException;

  /**
   * Retrieves the cached output for a specified UiLibrary.
   *
   * @param library UiLibrary to retrieve cached script for.
   * @param scriptType ScriptType (CSS or JS) to retrieve.
   * @param minified Whether to retrieved the minified version.
   * @return The cached output for a specified UiLibrary.
   * @throws CacheRetrievalException Cached output could not be retrieved.
   */
  String getCachedOutput(FrontendLibrary library, ScriptType scriptType, boolean minified)
      throws CacheRetrievalException;

  /**
   * Caches all scripts for the specified UiLibrary.
   *
   * @param libraryPath Path to cached library to cache.
   * @param output Compiled output.
   * @param scriptTypeInterface Script type that will be cached.
   * @param isMinified Whether the output is minified.
   * @throws CacheBuilderException Cache failed to build for specified UiLibrary.
   */
  @SuppressWarnings("RedundantThrows")
  void cacheUiLibraryScript(String libraryPath, String output,
      ScriptType scriptTypeInterface, boolean isMinified) throws CacheBuilderException;
}
