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

/**
 * Provides UI Library Configuration Settings.
 */
public interface UiLibraryConfigurationService extends ManagedService {

  /**
   * Name of the default CSS minifier.
   *
   * @return Name of the default CSS minifier.
   */
  String getDefaultCssMinifierName();

  /**
   * Name of the default JavaScript minifier.
   *
   * @return Name of the default JavaScript minifier.
   */
  String getDefaultJsMinifierName();

}