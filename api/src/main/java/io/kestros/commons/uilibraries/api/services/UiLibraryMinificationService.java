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
import io.kestros.commons.uilibraries.api.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import java.util.List;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Service for determining how CSS and JS scripts will be compressed/minified.
 */
public interface UiLibraryMinificationService extends ManagedService {

  /**
   * Whether a request should be minified.
   *
   * @param request Request.
   * @return Whether a request should be minified.
   */
  boolean isMinifiedRequest(SlingHttpServletRequest request);

  /**
   * All registered {@link ScriptMinifierService} instances.
   *
   * @return All registered {@link ScriptMinifierService} instances.
   */
  List<ScriptMinifierService> getMinificationServices();

  /**
   * All registered {@link ScriptMinifierService} instances, which can minify CSS scripts.
   *
   * @return All registered {@link ScriptMinifierService} instances, which can minify CSS scripts.
   */
  List<ScriptMinifierService> getCssMinificationServices();

  /**
   * All registered {@link ScriptMinifierService} instances, which can minify JavaScript scripts.
   *
   * @return All registered {@link ScriptMinifierService} instances, which can minify JavaScript
   *     scripts.
   */
  List<ScriptMinifierService> getJavaScriptMinificationServices();

  /**
   * Compressed the specified string as a ScriptType.
   *
   * @param scriptOutput string to compress.
   * @param scriptType ScriptType to compress the String as.
   * @return The specified String, compressed/minified.
   * @throws ScriptCompressionException Passed script failed to compress.
   */
  String getMinifiedOutput(String scriptOutput, ScriptType scriptType)
      throws ScriptCompressionException;
}