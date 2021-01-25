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
import io.kestros.commons.uilibraries.api.exceptions.LibraryRetrievalException;
import io.kestros.commons.uilibraries.api.models.UiLibraryInterface;

/**
 * Provides {@link UiLibraryInterface} instances.
 */
public interface UiLibraryRetrievalService extends ManagedService {

  /**
   * Retrieves a specified UiLibrary.
   *
   * @param path Path to the UiLibrary resource.
   * @return UiLibrary.
   * @throws LibraryRetrievalException Failed to retrieve a UiLibrary at the given path.
   */
  UiLibraryInterface getUiLibrary(String path) throws LibraryRetrievalException;

}