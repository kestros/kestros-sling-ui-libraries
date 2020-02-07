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

package io.kestros.commons.uilibraries.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true,
           service = UiLibraryConfigurationService.class)
public class UiLibraryConfigurationService implements Serializable {

  private static final long serialVersionUID = -5412418185436722061L;

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryConfigurationService.class);

  @Reference(service = UiLibraryConfigurationFactory.class,
             cardinality = ReferenceCardinality.OPTIONAL,
             policy = ReferencePolicy.DYNAMIC,
             bind = "bindUiLibraryConfigurationServiceFactory",
             unbind = "unbindUiLibraryConfigurationServiceFactory")
  private volatile List<UiLibraryConfigurationFactory> configurationList;

  protected void bindUiLibraryConfigurationServiceFactory(
      final UiLibraryConfigurationFactory config) {
    LOG.info("Binding new UiLibraryConfiguration to UiLibraryConfigurationService");
    if (configurationList == null) {
      configurationList = new ArrayList<>();
    }
    configurationList.add(config);
  }

  protected synchronized void unbindUiLibraryConfigurationServiceFactory(
      final UiLibraryConfigurationFactory config) {
    LOG.info("Unbinding UiLibraryConfiguration from UiLibraryConfigurationService");
    configurationList.remove(config);
  }

  /**
   * List of UILibrary paths that should be minified. Compiled from all UiLibraryConfigurations
   *
   * @return List of UILibrary paths that should be minified. Compiled from all
   *     UiLibraryConfigurations
   */
  @Nonnull
  public List<String> getMinifiedLibraryPaths() {
    final List<String> minifiedPaths = new ArrayList<>();
    if (configurationList != null) {
      for (final Object config : configurationList) {
        final UiLibraryConfigurationFactory factoryConfig = (UiLibraryConfigurationFactory) config;
        minifiedPaths.addAll(factoryConfig.getMinifiedLibraryPaths());
      }
      return minifiedPaths;
    }
    LOG.warn("Unable to determine if there are any UiLibraryConfigurations.  No scripts will"
             + "be minified.");
    return Collections.emptyList();
  }
}
