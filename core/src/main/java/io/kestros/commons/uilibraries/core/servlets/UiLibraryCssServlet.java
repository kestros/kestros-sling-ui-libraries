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

package io.kestros.commons.uilibraries.core.servlets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.uilibraries.api.exceptions.LibraryRetrievalException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryRetrievalService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UiLibrary CSS endpoint servlet.
 */
@Component(immediate = true,
           service = Servlet.class,
           property = {"sling.servlet.resourceTypes=kes:UiLibrary", "sling.servlet.extensions=css",
               "sling.servlet.methods=GET"})
public class UiLibraryCssServlet extends BaseUiLibraryServlet {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryCssServlet.class);
  private static final long serialVersionUID = -3258550726901179862L;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryRetrievalService uiLibraryRetrievalService;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryMinificationService uiLibraryMinificationService;

  @Override
  protected <T extends FrontendLibrary> T getLibrary(String libraryPath) {
    if (uiLibraryRetrievalService != null) {
      try {
        return (T) uiLibraryRetrievalService.getUiLibrary(libraryPath);
      } catch (LibraryRetrievalException e) {
        LOG.error("Unable to retrieve library {}, {}.", libraryPath, e.getMessage());
      }
    }
    return null;
  }

  @Override
  protected UiLibraryCompilationService getUiLibraryCompilationService() {
    return uiLibraryCompilationService;
  }

  @Override
  protected UiLibraryConfigurationService getUiLibraryConfigurationService() {
    return uiLibraryConfigurationService;
  }

  @Override
  protected UiLibraryMinificationService getUiLibraryMinificationService() {
    return uiLibraryMinificationService;
  }

  @Override
  protected UiLibraryCacheService getUiLibraryCacheService() {
    return uiLibraryCacheService;
  }

  @Override
  protected ScriptTypes getScriptType() {
    return ScriptTypes.CSS;
  }
}
