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

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common logic for frontend library css/js servlets.
 */
public abstract class BaseUiLibraryServlet extends SlingSafeMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(BaseUiLibraryServlet.class);
  private static final long serialVersionUID = 1L;

  @Nullable
  protected abstract <T extends FrontendLibrary> T getLibrary(@Nonnull final String libraryPath,
          @Nonnull final ResourceResolver resourceResolver);

  @Nullable
  @Deprecated
  protected abstract <T extends FrontendLibrary> T getLibrary(@Nonnull final String libraryPath);

  @Nullable
  protected abstract UiLibraryCompilationService getUiLibraryCompilationService();

  @Nullable
  protected abstract UiLibraryConfigurationService getUiLibraryConfigurationService();

  @Nullable
  protected abstract UiLibraryMinificationService getUiLibraryMinificationService();

  @Nullable
  protected abstract UiLibraryCacheService getUiLibraryCacheService();

  @Nonnull
  protected abstract ScriptTypes getScriptType();

  /**
   * Writes the GET response for the current UiLibrary.
   *
   * @param request SlingHttpServletRequest
   * @param response SlingHttpServletResponse to write to.
   */
  @Override
  public void doGet(@Nonnull final SlingHttpServletRequest request,
          @Nonnull final SlingHttpServletResponse response) {
    String libraryPath = request.getResource().getPath();
    Boolean isMinified = Boolean.FALSE;
    LOG.debug("Building {} response for library {}.",
            getScriptType().getName().replaceAll("[\r\n]", ""),
            libraryPath.replaceAll("[\r\n]", ""));

    if (getUiLibraryMinificationService() != null) {
      isMinified = getUiLibraryMinificationService().isMinifiedRequest(request);
    }

    String output = getCachedOutputOrEmptyString(libraryPath, isMinified,
            request.getResourceResolver());
    if (StringUtils.isNotEmpty(output)) {
      try {
        response.setContentType(getScriptType().getOutputContentType());
        writeResponse(output, SlingHttpServletResponse.SC_OK, response);
        return;
      } catch (IOException e) {
        LOG.error("Unable to write cached script output for {}, script type: {}. IOException: {}",
                libraryPath.replaceAll("[\r\n]", ""),
                getScriptType().getName().replaceAll("[\r\n]", ""),
                e.getMessage().replaceAll("[\r\n]", ""));
      }

    }
    FrontendLibrary library = getLibrary(libraryPath, request.getResourceResolver());
    if (library != null) {
      try {
        output = getUiLibraryCompilationService().getUiLibraryOutput(library, getScriptType(),
                request.getResourceResolver());

        if (output.startsWith("<h1>")) {
          response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          response.setContentType("text/html");
          writeResponse(output, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
          return;
        }

        if (isMinified) {
          output = getUiLibraryMinificationService().getMinifiedOutput(output, getScriptType());
        }

        response.setContentType(getScriptType().getOutputContentType());
        writeResponse(output, SlingHttpServletResponse.SC_OK, response);

        try {
          cacheOutput(library, output, getScriptType(), isMinified, request.getResourceResolver());
          return;
        } catch (Exception e) {
          LOG.error("Could not cache {} script for {}. {}.",
                  getScriptType().getName().replaceAll("[\r\n]", ""),
                  libraryPath.replaceAll("[\r\n]", ""),
                  e.getMessage().replaceAll("[\r\n]", ""));
          return;
        }
      } catch (Exception e) {

        LOG.error("Could not render {} script for {}. {}.",
                getScriptType().getName().replaceAll("[\r\n]", ""),
                libraryPath.replaceAll("[\r\n]", ""),
                e.getMessage().replaceAll("[\r\n]", ""));
      }
    }
    response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
    response.setContentType("text/plain");
  }

  void cacheOutput(@Nonnull final FrontendLibrary library, @Nonnull final String content,
          @Nonnull final ScriptTypes scriptType,
          boolean minified, @Nonnull final ResourceResolver resourceResolver) {
    if (getUiLibraryCacheService() != null) {
      try {
        getUiLibraryCacheService().cacheUiLibraryScript(library.getPath(), content, scriptType,
                minified, resourceResolver);
      } catch (CacheBuilderException e) {
        LOG.warn("Unable to build cache for library {}. {}",
                library.getPath().replaceAll("[\r\n]", ""),
                e.getMessage().replaceAll("[\r\n]", ""));
      }
    }
  }

  @Nonnull
  String getCachedOutputOrEmptyString(@Nonnull final String libraryPath,
          @Nonnull final Boolean isMinified,
          @Nonnull final ResourceResolver resourceResolver) {
    if (getUiLibraryCacheService() != null) {
      try {
        return getUiLibraryCacheService().getCachedOutput(libraryPath, getScriptType(), isMinified,
                resourceResolver);
      } catch (CacheRetrievalException e) {
        LOG.debug("Unable to retrieve cached value for {}. {}",
                libraryPath.replaceAll("[\r\n]", ""), e.getMessage().replaceAll("[\r\n]", ""));
      }
    } else {
      LOG.warn("Unable to retrieve cached scripts for {}.  UiLibrary cache service not detected.",
              libraryPath.replaceAll("[\r\n]", ""));
    }
    return StringUtils.EMPTY;
  }

  void writeResponse(@Nonnull final String output, final int responseStatus,
          @Nonnull final SlingHttpServletResponse response)
          throws IOException {
    response.getWriter().write(output);
    response.setContentType(getScriptType().getOutputContentType());
    if (responseStatus != SlingHttpServletResponse.SC_OK) {
      response.setStatus(responseStatus);
    }
  }
}