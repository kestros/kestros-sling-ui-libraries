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

package io.kestros.commons.uilibraries.core.services.impl;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.uilibraries.api.exceptions.LibraryRetrievalException;
import io.kestros.commons.uilibraries.api.models.UiLibrary;
import io.kestros.commons.uilibraries.api.services.UiLibraryRetrievalService;
import io.kestros.commons.uilibraries.core.UiLibraryResource;
import java.util.Collections;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Retrieve {@link UiLibraryResource} Sling Models.
 */
@Component(immediate = true,
           service = UiLibraryRetrievalService.class)
public class UiLibraryRetrievalServiceImpl extends BaseServiceResolverService
    implements UiLibraryRetrievalService {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  public UiLibrary getUiLibrary(String path) throws LibraryRetrievalException {
    try {
      return getResourceAsType(path, getServiceResourceResolver(), UiLibraryResource.class);
    } catch (Exception e) {
      throw new LibraryRetrievalException(e.getMessage());
    }
  }

  @Override
  protected String getServiceUserName() {
    return "ui-library-retrieval";
  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    return Collections.emptyList();
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  public String getDisplayName() {
    return "UI Library Retrieval Service";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
  }

}
