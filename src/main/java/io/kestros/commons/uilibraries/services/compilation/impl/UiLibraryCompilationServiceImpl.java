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

package io.kestros.commons.uilibraries.services.compilation.impl;

import static io.kestros.commons.uilibraries.utils.UiLibraryUtils.getScriptOutput;

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.services.minification.UiLibraryMinificationService;
import javax.annotation.Nonnull;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compiles CSS and JS for {@link UiLibrary}.
 */
@Component(immediate = true,
           service = UiLibraryCompilationService.class)
public class UiLibraryCompilationServiceImpl implements UiLibraryCompilationService {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryCompilationServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryMinificationService uiLibraryMinificationService;

  @Override
  public String getDisplayName() {
    return "UI Library Compilation Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {

  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {

  }

  @Override
  public String getUiLibraryOutput(final @Nonnull UiLibrary uiLibrary,
      final @Nonnull ScriptType scriptType, final @Nonnull Boolean minify)
      throws InvalidResourceTypeException {
    String output = getDependenciesOutput(uiLibrary, scriptType);

    output += getScriptOutput(scriptType, uiLibrary, false);

    if (minify) {
      if (uiLibraryMinificationService != null) {
        try {
          return uiLibraryMinificationService.getMinifiedOutput(output, scriptType);
        } catch (final ScriptCompressionException e) {
          LOG.warn("Unable to minify {} output for {}. {}", scriptType.getName(),
              uiLibrary.getPath(), e.getMessage());
        }
      }
    }
    return output;
  }

  private String getDependenciesOutput(final UiLibrary uiLibrary, final ScriptType scriptType)
      throws InvalidResourceTypeException {
    final StringBuilder output = new StringBuilder();

    for (final UiLibrary dependency : uiLibrary.getDependencies()) {
      output.append(dependency.getOutput(scriptType, false));
    }
    return output.toString();
  }

}
