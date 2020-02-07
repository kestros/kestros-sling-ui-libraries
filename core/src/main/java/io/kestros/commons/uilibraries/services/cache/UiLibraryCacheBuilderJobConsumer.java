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

package io.kestros.commons.uilibraries.services.cache;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = JobConsumer.class,
           property = JobConsumer.PROPERTY_TOPICS + "=kestros/ui-libraries/cache")
public class UiLibraryCacheBuilderJobConsumer implements JobConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryCacheBuilderJobConsumer.class);

  @Reference
  private UiLibraryCacheService uiLibraryCacheService;

  @Override
  public JobResult process(final Job job) {

    final Object uiLibraryPath = job.getProperty("ui-library-path");
    final Object cacheMinifiedProperty = job.getProperty("cache-minified");
    if (uiLibraryPath instanceof String) {
      Boolean cacheMinified = false;
      if (cacheMinifiedProperty instanceof Boolean) {
        cacheMinified = (Boolean) cacheMinifiedProperty;
      }
      try {
        uiLibraryCacheService.cacheUiLibraryScripts((String) uiLibraryPath, cacheMinified);
        return JobResult.OK;
      } catch (final CacheBuilderException exception) {
        LOG.error("Unable to cache UiLibrary {}. {}", uiLibraryPath,
            exception.getMessage());
      }
    }
    return JobResult.FAILED;
  }
}