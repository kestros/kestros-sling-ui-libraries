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

package io.kestros.commons.uilibraries.core.healthchecks;

import io.kestros.commons.osgiserviceutils.healthchecks.BaseManagedServiceHealthCheck;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.uilibraries.api.services.UiLibraryConfigurationService;
import org.apache.felix.hc.api.HealthCheck;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Health Check for {@link UiLibraryConfigurationService}.
 */
//@SuppressFBWarnings("RI_REDUNDANT_INTERFACES")
//@Component
//@HealthCheckService(name = "UI Library Configuration Service Health Check",
//                    tags = {"kestros", "ui-libraries"})
//@Async(intervalInSec = 60)
//@ResultTTL(resultCacheTtlInMs = 10000)
//@HealthCheckMBean(name = "UiLibraryConfigurationServiceHealthCheck")
//@Sticky(keepNonOkResultsStickyForSec = 10)
public class UiLibraryConfigurationServiceHealthCheck extends BaseManagedServiceHealthCheck
    implements HealthCheck {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @Override
  public ManagedService getManagedService() {
    return uiLibraryConfigurationService;
  }

  @Override
  public String getServiceName() {
    return "UI Library Configuration Service";
  }
}
