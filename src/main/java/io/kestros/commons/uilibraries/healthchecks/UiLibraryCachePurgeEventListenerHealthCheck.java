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

package io.kestros.commons.uilibraries.healthchecks;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.osgiserviceutils.healthchecks.BaseManagedServiceHealthCheck;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.uilibraries.eventlisteners.UiLibraryCachePurgeEventListener;
import org.apache.felix.hc.annotation.Async;
import org.apache.felix.hc.annotation.HealthCheckMBean;
import org.apache.felix.hc.annotation.HealthCheckService;
import org.apache.felix.hc.annotation.ResultTTL;
import org.apache.felix.hc.annotation.Sticky;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Health Check for {@link UiLibraryCachePurgeEventListener}.
 */
@SuppressFBWarnings("RI_REDUNDANT_INTERFACES")
@Component
@HealthCheckService(name = "UI Library Cache Purge Event ListenerHealth Check",
                    tags = {"kestros", "ui-libraries"})
@Async(intervalInSec = 60)
@ResultTTL(resultCacheTtlInMs = 10000)
@HealthCheckMBean(name = "UiLibraryCachePurgeEventListenerServiceHealthCheck")
@Sticky(keepNonOkResultsStickyForSec = 10)
public class UiLibraryCachePurgeEventListenerHealthCheck extends BaseManagedServiceHealthCheck {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCachePurgeEventListener uiLibraryCachePurgeEventListener;

  @Override
  public ManagedService getCacheService() {
    return uiLibraryCachePurgeEventListener;
  }

  @Override
  public String getServiceName() {
    return "UI Library Cache Purge Event Listener";
  }
}
