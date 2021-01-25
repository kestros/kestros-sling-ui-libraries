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

import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.models.ScriptFile;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.CssScriptTypeCompilerService;
import io.kestros.commons.uilibraries.api.services.JavaScriptScriptTypeCompilerService;
import io.kestros.commons.uilibraries.api.services.ScriptTypeCompiler;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up compiler services to provide compiled CSS or JavaScript output for UiLibraries.
 */
@Component(immediate = true,
           service = UiLibraryCompilationService.class)
public class UiLibraryCompilationServiceImpl implements UiLibraryCompilationService {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryCompilationServiceImpl.class);

  private ComponentContext context;

  @Override
  public String getDisplayName() {
    return "UI Library Compilation Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {
    this.context = componentContext;
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    try {
      getCompiler(Collections.singletonList(
          ScriptTypes.CSS), getCssCompilers());
    } catch (NoMatchingCompilerException e) {
      log.critical("CSS Compiler not found.");
    }
    try {
      getCompiler(Collections.singletonList(
          ScriptTypes.JAVASCRIPT), getJavaScriptCompilers());
    } catch (NoMatchingCompilerException e) {
      log.critical("JavaScript Compiler not found.");
    }
  }

  @Override
  public List<ScriptType> getAllRegisteredScriptTypes() {
    List<ScriptType> scriptTypeList = new ArrayList<>();
    scriptTypeList.addAll(getRegisteredCssScriptTypes());
    scriptTypeList.addAll(getRegisteredJavaScriptScriptTypes());
    return scriptTypeList;
  }

  @Override
  public List<ScriptType> getRegisteredCssScriptTypes() {
    List<ScriptType> scriptTypeList = new ArrayList<>();
    for (CssScriptTypeCompilerService cssCompiler : getCssCompilers()) {
      for (ScriptType scriptType : cssCompiler.getScriptTypes()) {
        if (!scriptTypeList.contains(scriptType)) {
          scriptTypeList.add(scriptType);
        }
      }
    }
    return scriptTypeList;
  }

  @Override
  public List<ScriptType> getRegisteredJavaScriptScriptTypes() {
    List<ScriptType> scriptTypeList = new ArrayList<>();
    for (JavaScriptScriptTypeCompilerService javaScriptCompiler : getJavaScriptCompilers()) {
      for (ScriptType scriptType : javaScriptCompiler.getScriptTypes()) {
        if (!scriptTypeList.contains(scriptType)) {
          scriptTypeList.add(scriptType);
        }
      }
    }
    return scriptTypeList;
  }

  @Override
  public List<CssScriptTypeCompilerService> getCssCompilers() {
    return getAllOsgiServicesOfType(context, CssScriptTypeCompilerService.class);
  }

  @Override
  public List<JavaScriptScriptTypeCompilerService> getJavaScriptCompilers() {
    return getAllOsgiServicesOfType(context, JavaScriptScriptTypeCompilerService.class);
  }

  @Override
  public List<ScriptTypeCompiler> getCompilers() {
    return getAllOsgiServicesOfType(context, ScriptTypeCompiler.class);
  }

  @Override
  public <T extends ScriptTypeCompiler> ScriptTypeCompiler getCompiler(
      List<ScriptType> scriptTypes, List<T> registeredCompilers)
      throws NoMatchingCompilerException {
    for (ScriptTypeCompiler compiler : registeredCompilers) {
      List<ScriptType> compilerScriptTypes = compiler.getScriptTypes();
      if (compilerScriptTypes.size() >= scriptTypes.size() && compilerScriptTypes.containsAll(
          scriptTypes)) {
        return compiler;
      }
    }

    StringBuilder scriptTypesStringBuilder = new StringBuilder();
    for (ScriptType scriptType : scriptTypes) {
      if (StringUtils.isNotEmpty(scriptTypesStringBuilder.toString())) {
        scriptTypesStringBuilder.append(" ");
      }
      scriptTypesStringBuilder.append(scriptType.getName());
    }
    throw new NoMatchingCompilerException(
        String.format("No compiler registered for ScriptType(s): %s.",
            scriptTypesStringBuilder.toString()));
  }

  @Override
  public String getUiLibraryOutput(FrontendLibrary uiLibrary, ScriptType scriptType,
      Boolean minify) throws InvalidResourceTypeException, NoMatchingCompilerException {
    ScriptTypeCompiler compiler = getCompiler(
        getLibraryScriptTypes(uiLibrary, scriptType.getRootResourceName()), getCompilers());
    StringBuilder rawOutputStringBuilder = new StringBuilder();
    for (ScriptFile scriptFile : uiLibrary.getScriptFiles(compiler.getScriptTypes(),
        scriptType.getRootResourceName())) {
      try {
        String fileContent = scriptFile.getFileContent();
        if (StringUtils.isNotEmpty(rawOutputStringBuilder.toString()) && StringUtils.isNotEmpty(
            fileContent)) {
          rawOutputStringBuilder.append("\n");
        }
        rawOutputStringBuilder.append(scriptFile.getFileContent());

      } catch (IOException e) {
        LOG.error("Unable to append {} file {} to UiLibrary output due to IOException",
            scriptFile.getFileType().getFileModelClass(), scriptFile.getName());
      }
    }
    return compiler.getOutput(rawOutputStringBuilder.toString());
  }

  List<ScriptType> getLibraryScriptTypes(FrontendLibrary library, String folderName) {
    List<ScriptType> scriptTypes = new ArrayList<>();
    for (ScriptFile scriptFile : library.getScriptFiles(getAllRegisteredScriptTypes(),
        folderName)) {
      if (!scriptTypes.contains(scriptFile.getFileType())) {
        scriptTypes.add((ScriptType) scriptFile.getFileType());
      }
    }
    return scriptTypes;
  }

}
