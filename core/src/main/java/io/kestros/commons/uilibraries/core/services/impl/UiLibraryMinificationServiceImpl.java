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

import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getAllOsgiServicesOfType;

import io.kestros.commons.uilibraries.api.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.ScriptMinifierService;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.api.services.UiLibraryMinificationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Finds registered {@link ScriptMinifierService} instances to minify CSS and JavaScript.
 */
@Component(immediate = true,
           service = UiLibraryMinificationService.class)
public class UiLibraryMinificationServiceImpl implements UiLibraryMinificationService {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  private ComponentContext componentContext;

  @Override
  public String getDisplayName() {
    return "UI Library Minification Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {
    this.componentContext = componentContext;
  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    if (getCssMinificationServices().size() == 0) {
      log.warn("No CSS minification services have been registered.");
    }
    if (getJavaScriptMinificationServices().size() == 0) {
      log.warn("No JavaScript minification services have been registered.");
    }
  }

  @Override
  public boolean isMinifiedRequest(SlingHttpServletRequest request) {
    List<String> selectors = Arrays.asList(request.getRequestPathInfo().getSelectors());
    return selectors.contains("min");
  }

  @Override
  public List<ScriptMinifierService> getMinificationServices() {
    return getAllOsgiServicesOfType(componentContext, ScriptMinifierService.class);
  }

  @Override
  public List<ScriptMinifierService> getCssMinificationServices() {
    return getMinificationServicesForScriptType(
        io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptType.CSS);
  }

  @Override
  public List<ScriptMinifierService> getJavaScriptMinificationServices() {
    return getMinificationServicesForScriptType(
        io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptType.JAVASCRIPT);
  }

  @Override
  public String getMinifiedOutput(String scriptOutput, ScriptType scriptType)
      throws ScriptCompressionException {
    List<ScriptMinifierService> minifierServices = getMinificationServicesForScriptType(scriptType);
    if (CollectionUtils.isNotEmpty(minifierServices)) {
      return minifierServices.get(0).getMinifiedScript(scriptOutput, scriptType);
    }
    return scriptOutput;
  }

  private List<ScriptMinifierService> getMinificationServicesForScriptType(
      ScriptType scriptType) {
    List<ScriptMinifierService> scriptMinifierServiceList = new ArrayList<>();
    for (ScriptMinifierService minifierService : getMinificationServices()) {
      if (minifierService.getSupportedScriptTypes().contains(scriptType)) {
        scriptMinifierServiceList.add(minifierService);
      }
    }
    return scriptMinifierServiceList;
  }
}
