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

package io.kestros.commons.uilibraries.servlets;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptTo;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseUiLibraryServlet extends SlingSafeMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(BaseUiLibraryServlet.class);

  private static final long serialVersionUID = -25468213891168172L;

  protected abstract Class<? extends UiLibrary> getUiLibraryClass();

  protected abstract UiLibraryConfigurationService getUiLibraryConfigurationService();

  protected abstract UiLibraryCacheService getUiLibraryCacheService();

  protected abstract ScriptType getScriptType();

  protected String getOutput(final SlingHttpServletRequest request, final boolean minify)
      throws InvalidResourceTypeException {
    return getUiLibrary(request, getUiLibraryClass()).getOutput(getScriptType(), minify);
  }

  /**
   * Writes the GET response for the current UiLibrary.
   *
   * @param request SlingHttpServletRequest
   * @param response SlingHttpServletResponse to write to.
   */
  @Override
  public void doGet(@Nonnull final SlingHttpServletRequest request,
      @Nonnull final SlingHttpServletResponse response) {

    boolean performCache = false;
    String output = null;
    UiLibrary uiLibrary = null;
    try {
      uiLibrary = getUiLibrary(request, getUiLibraryClass());
      output = getCachedOutput(uiLibrary, request);

      if (StringUtils.isBlank(output)) {
        performCache = true;

        LOG.info("getting uncached ui library");
        output = getOutput(request, isMinified(request));
      }

    } catch (final InvalidResourceTypeException exception) {
      response.setStatus(400);
      LOG.error("Unable to render script for {} due InvalidResourceTypeException. {}",
          request.getPathInfo(), exception.getMessage());
    }

    if (StringUtils.isNotEmpty(output)) {
      try {
        response.setContentType(getScriptType().getOutputContentType());
        write(output, response);
      } catch (final IOException exception) {
        response.setContentType("text/plain");
        response.setStatus(400);
        LOG.error("Unable to render script for {} due to IOException. {}", request.getPathInfo(),
            exception.getMessage());
      }
    }
    if (getUiLibraryCacheService() != null && performCache && uiLibrary != null) {
      try {
        getUiLibraryCacheService().cacheUiLibraryScripts(uiLibrary, isMinified(request));
      } catch (final CacheBuilderException e) {
        LOG.warn("Unable to build UiLibrary Cache. {}", e.getMessage());
      }
    }
  }

  String getCachedOutput(final UiLibrary uiLibrary, final SlingHttpServletRequest request) {
    try {
      if (getUiLibraryCacheService() != null) {
        return getUiLibraryCacheService().getCachedOutput(uiLibrary, getScriptType(),
            isMinified(request));
      } else {
        LOG.warn("Unable to retrieve cached scripts for {}.  UiLibrary cache service not detected.",
            uiLibrary.getPath());
      }
    } catch (final CacheRetrievalException exception) {
      LOG.debug("Unable to retrieve cached value for {}. {}", uiLibrary.getPath(),
          exception.getMessage());
    }
    return StringUtils.EMPTY;
  }

  boolean isMinified(final SlingHttpServletRequest request) {
    if (getUiLibraryConfigurationService() != null) {
      return getUiLibraryConfigurationService().getMinifiedLibraryPaths().contains(
          request.getResource().getPath());
    } else {
      LOG.error("Unable to determine whether UiLibraries should be minified due to null/disabled "
                + "UiLibraryConfigurationService");
    }
    return false;
  }

  <T extends UiLibrary> T getUiLibrary(final SlingHttpServletRequest request, final Class<T> type)
      throws InvalidResourceTypeException {
    return adaptTo(request.getResource(), type);
  }

  void write(final String output, final SlingHttpServletResponse response) throws IOException {
    response.getWriter().write(output);
  }

}
