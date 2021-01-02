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

package io.kestros.commons.uilibraries.eventlisteners;

import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getAllOsgiServicesOfType;

import io.kestros.commons.osgiserviceutils.services.eventlisteners.impl.BaseCachePurgeOnResourceChangeEventListener;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Purges cached UiLibrary CSS and JavaScript on caches after changes under /apps, /etc, and /libs.
 */
@Component(service = {ResourceChangeListener.class, UiLibraryCachePurgeEventListener.class},
           property = {ResourceChangeListener.CHANGES + "=ADDED",
               ResourceChangeListener.CHANGES + "=CHANGED",
               ResourceChangeListener.CHANGES + "=REMOVED",
               ResourceChangeListener.CHANGES + "=PROVIDER_ADDED",
               ResourceChangeListener.CHANGES + "=PROVIDER_REMOVED",
               ResourceChangeListener.PATHS + "=/apps", ResourceChangeListener.PATHS + "=/etc",
               ResourceChangeListener.PATHS + "=/libs"},
           immediate = true)
public class UiLibraryCachePurgeEventListener extends BaseCachePurgeOnResourceChangeEventListener {

  private static final Logger LOG = LoggerFactory.getLogger(
      BaseCachePurgeOnResourceChangeEventListener.class);

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  public String getDisplayName() {
    return "UI Library Cache Purge Event Listener";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating UiLibraryCachePurgeEventListener.");
  }

  @Override
  protected String getServiceUserName() {
    return "ui-library-cache-purge";
  }

  @Override
  protected boolean purgeOnActivation() {
    return false;
  }

  @Override
  public List<UiLibraryCacheService> getCacheServices() {
    return getAllOsgiServicesOfType(getComponentContext(), UiLibraryCacheService.class);
  }

  @Override
  public ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

}
