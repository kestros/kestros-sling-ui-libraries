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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.osgiserviceutils.services.BaseServiceResolverService;
import io.kestros.commons.uilibraries.api.exceptions.LibraryRetrievalException;
import io.kestros.commons.uilibraries.api.models.UiLibrary;
import io.kestros.commons.uilibraries.api.services.UiLibraryRetrievalService;
import io.kestros.commons.uilibraries.core.UiLibraryResource;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Retrieve {@link UiLibraryResource} Sling Models.
 */
@SuppressFBWarnings({"IMC_IMMATURE_CLASS_NO_TOSTRING"})
@Component(immediate = true,
        service = UiLibraryRetrievalService.class)
public class UiLibraryRetrievalServiceImpl extends BaseServiceResolverService
        implements UiLibraryRetrievalService {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryRetrievalServiceImpl.class);
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
          policyOption = ReferencePolicyOption.GREEDY)
  private ResourceResolverFactory resourceResolverFactory;

  @Nonnull
  @Override
  public UiLibrary getUiLibrary(@Nonnull String path, @Nonnull ResourceResolver resourceResolver)
          throws LibraryRetrievalException {
    try {
      return getResourceAsType(path, resourceResolver, UiLibraryResource.class);
    } catch (Exception e) {
      throw new LibraryRetrievalException(e.getMessage(), e);
    }
  }

  @Nonnull
  @Override
  public UiLibrary getUiLibrary(@Nonnull String path) throws LibraryRetrievalException {
    try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
      return getUiLibrary(path, resourceResolver);
    } catch (LoginException e) {
      throw new LibraryRetrievalException(e.getMessage(), e);
    }

  }

  @Nonnull
  @Override
  protected String getServiceUserName() {
    return "ui-library-manager";
  }

  @Nonnull
  @Override
  protected Logger getLogger() {
    return LOG;
  }

  @Nonnull
  @Override
  protected List<String> getRequiredResourcePaths() {
    return Collections.emptyList();
  }

  @Nullable
  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Nonnull
  @Override
  public String getDisplayName() {
    return "UI Library Retrieval Service";
  }

}
