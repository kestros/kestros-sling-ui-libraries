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

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class JcrFileUiLibraryCacheServiceTest {

    @Rule
    public SlingContext context = new SlingContext();

    private JcrFileUiLibraryCacheService jcrFileUiLibraryCacheService;
    private JobManager jobManager;

    @Before
    public void setUp() throws Exception {
        jcrFileUiLibraryCacheService = new JcrFileUiLibraryCacheService();
        jobManager = mock(JobManager.class);
    }

    @Test
    public void getJobManager() {
        context.registerService(JobManager.class, jobManager);
        context.registerInjectActivateService(jcrFileUiLibraryCacheService);
        assertNotNull(jcrFileUiLibraryCacheService.getJobManager());
    }

    @Test
    public void getDisplayName() {
        assertEquals("Ui Library Cache Service", jcrFileUiLibraryCacheService.getDisplayName());
    }

    @Test
    public void afterCachePurgeComplete() {
    }

    @Test
    public void getMinimumTimeBetweenCachePurges() {
    }

    @Test
    public void getCacheCreationJobName() {
    }

    @Test
    public void getServiceCacheRootPath() {
    }

    @Test
    public void getServiceUserName() {
    }

    @Test
    public void getLogger() {
    }

    @Test
    public void getResourceResolverFactory() {
    }

    @Test
    public void getRequiredResourcePaths() {
    }

    @Test
    public void testGetCachedOutput() throws CacheBuilderException, CacheRetrievalException {
        context.registerService(JobManager.class, jobManager);
        context.registerInjectActivateService(jcrFileUiLibraryCacheService);
        context.create().resource("/path");
        context.create().resource("/path/jcr:content");
        context.create().resource("/var/cache/ui-libraries");
        jcrFileUiLibraryCacheService.cacheUiLibraryScript("/path","test", ScriptTypes.CSS, false, context.resourceResolver());
        assertEquals("test", jcrFileUiLibraryCacheService.getCachedOutput("/path", ScriptTypes.CSS, false, context.resourceResolver()));
    }

    @Test
    public void testGetCachedOutputWhenMinified() throws CacheBuilderException, CacheRetrievalException {
        context.registerService(JobManager.class, jobManager);
        context.registerInjectActivateService(jcrFileUiLibraryCacheService);
        context.create().resource("/path");
        context.create().resource("/path/jcr:content");
        context.create().resource("/var/cache/ui-libraries");
        jcrFileUiLibraryCacheService.cacheUiLibraryScript("/path","test", ScriptTypes.CSS, true, context.resourceResolver());
        assertEquals("test", jcrFileUiLibraryCacheService.getCachedOutput("/path", ScriptTypes.CSS, true, context.resourceResolver()));
    }

    @Test
    public void testGetCachedOutputWhenFileNotCached() throws CacheBuilderException, CacheRetrievalException {
        context.registerService(JobManager.class, jobManager);
        context.registerInjectActivateService(jcrFileUiLibraryCacheService);
        context.create().resource("/path");
        context.create().resource("/path/jcr:content");
        context.create().resource("/var/cache/ui-libraries");
        Exception exception = null;
        try {
            assertEquals("test", jcrFileUiLibraryCacheService.getCachedOutput("/path", ScriptTypes.CSS, true, context.resourceResolver()));
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals("Unable to adapt '/var/cache/ui-libraries/path.min.css': Resource not found.", exception.getMessage());
    }


    @Test
    public void cacheUiLibraryScript() {
    }
}