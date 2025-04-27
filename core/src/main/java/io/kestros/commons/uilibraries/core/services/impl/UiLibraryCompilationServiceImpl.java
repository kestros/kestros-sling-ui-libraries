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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.JcrFileReadException;
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
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up compiler services to provide compiled CSS or JavaScript output for UiLibraries.
 */
@SuppressFBWarnings({"IMC_IMMATURE_CLASS_NO_TOSTRING"})
@Component(immediate = true, service = UiLibraryCompilationService.class)
public class UiLibraryCompilationServiceImpl implements UiLibraryCompilationService {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryCompilationServiceImpl.class);

  protected ComponentContext context;

  @Nonnull
  @Override
  public String getDisplayName() {
    return "UI Library Compilation Service";
  }

  @Override
  public void activate(@Nonnull ComponentContext componentContext) {
    this.context = componentContext;
  }

  @Override
  public void deactivate(@Nonnull ComponentContext componentContext) {
  }

  @Override
  public void runAdditionalHealthChecks(@Nonnull FormattingResultLog log) {
    try {
      getCompiler(Collections.singletonList(ScriptTypes.CSS), getCssCompilers());
    } catch (NoMatchingCompilerException e) {
      log.critical("CSS Compiler not found.");
    }
    try {
      getCompiler(Collections.singletonList(ScriptTypes.JAVASCRIPT), getJavaScriptCompilers());
    } catch (NoMatchingCompilerException e) {
      log.critical("JavaScript Compiler not found.");
    }
  }

  @Nonnull
  @Override
  public List<ScriptType> getAllRegisteredScriptTypes() {
    List<ScriptType> scriptTypeList = new ArrayList<>();
    scriptTypeList.addAll(getRegisteredCssScriptTypes());
    scriptTypeList.addAll(getRegisteredJavaScriptScriptTypes());
    return scriptTypeList;
  }

  @Nonnull
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

  @Nonnull
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

  @Nonnull
  @Override
  public List<CssScriptTypeCompilerService> getCssCompilers() {
    return getAllOsgiServicesOfType(context, CssScriptTypeCompilerService.class);
  }

  @Nonnull
  @Override
  public List<JavaScriptScriptTypeCompilerService> getJavaScriptCompilers() {
    return getAllOsgiServicesOfType(context, JavaScriptScriptTypeCompilerService.class);
  }

  @Nonnull
  @Override
  public List<ScriptTypeCompiler> getCompilers() {
    return getAllOsgiServicesOfType(context, ScriptTypeCompiler.class);
  }

  @Nonnull
  @Override
  public <T extends ScriptTypeCompiler> ScriptTypeCompiler getCompiler(
      @Nonnull List<ScriptType> scriptTypes, List<T> registeredCompilers) throws
      NoMatchingCompilerException {
    List<ScriptTypeCompiler> compatibleCompilers = new ArrayList<>();
    for (ScriptTypeCompiler compiler : registeredCompilers) {
      List<ScriptType> compilerScriptTypes = compiler.getScriptTypes();
      if (compilerScriptTypes.size() >= scriptTypes.size() && compilerScriptTypes.containsAll(
          scriptTypes)) {
        compatibleCompilers.add(compiler);
      }
    }
    // find the best matching, so that CSS only libraries will never be run through compilers that
    // could break them.
    ScriptTypeCompiler compiler = null;
    int bestMatch = 999;
    if (scriptTypes.isEmpty()) {
      for (ScriptTypeCompiler compatibleCompiler : compatibleCompilers) {
        if (compatibleCompiler.getScriptTypes().size() == 1) {
          compiler = compatibleCompiler;
          break;
        } else {
          compiler = compatibleCompiler;
        }
      }
    } else if (scriptTypes.size() == 1) {
      for (ScriptTypeCompiler compatibleCompiler : compatibleCompilers) {
        if (compatibleCompiler.getScriptTypes().size() == 1) {
          compiler = compatibleCompiler;
          break;
        } else {
          compiler = compatibleCompiler;
        }
      }
    } else {
      for (ScriptTypeCompiler compatibleCompiler : compatibleCompilers) {
        if (compatibleCompiler.getScriptTypes().size() < bestMatch) {
          bestMatch = compatibleCompiler.getScriptTypes().size();
          compiler = compatibleCompiler;
        }
      }
    }
    if (compiler != null) {
      return compiler;
    }

    StringBuilder scriptTypesStringBuilder = new StringBuilder();
    for (ScriptType scriptType : scriptTypes) {
      if (StringUtils.isNotEmpty(scriptTypesStringBuilder.toString())) {
        scriptTypesStringBuilder.append(' ');
      }
      scriptTypesStringBuilder.append(scriptType.getName());
    }
    throw new NoMatchingCompilerException(
        String.format("No compiler registered for ScriptType(s): %s.",
            scriptTypesStringBuilder.toString()));
  }

  @Nonnull
  @Override
  public String getUiLibraryOutput(@Nonnull FrontendLibrary library, ScriptType scriptType,
      @Nonnull ResourceResolver resourceResolver) throws InvalidResourceTypeException,
      NoMatchingCompilerException {
    ScriptTypeCompiler compiler = getCompiler(
        getLibraryScriptTypes(library, scriptType.getRootResourceName()), getCompilers());
    String uiLibrarySource = getUiLibrarySource(library, scriptType, resourceResolver);
    return compiler.getOutput(uiLibrarySource);
  }

  @Nonnull
  @Override
  public String getUiLibraryOutput(@Nonnull FrontendLibrary uiLibrary, ScriptType scriptType) throws
      InvalidResourceTypeException, NoMatchingCompilerException {
    LOG.warn("getUiLibraryOutput(FrontendLibrary, ScriptType) is deprecated. Use getUiLibraryOutput"
             + "(FrontendLibrary, ScriptType, ResourceResolver) instead.");
    ScriptTypeCompiler compiler = getCompiler(
        getLibraryScriptTypes(uiLibrary, scriptType.getRootResourceName()), getCompilers());
    return compiler.getOutput(getUiLibrarySource(uiLibrary, scriptType));
  }

  @Nonnull
  @Override
  public String getUiLibrarySource(@Nonnull FrontendLibrary library, ScriptType scriptType,
      @Nonnull ResourceResolver resourceResolver) throws InvalidResourceTypeException,
      NoMatchingCompilerException {
    ScriptTypeCompiler compiler = getCompiler(
        getLibraryScriptTypes(library, scriptType.getRootResourceName()), getCompilers());
    StringBuilder rawOutputStringBuilder = new StringBuilder();

    for (ScriptFile scriptFile : library.getScriptFiles(compiler.getScriptTypes(),
        scriptType.getRootResourceName())) {

      try {
        String fileContent = scriptFile.getFileContent();
        if (StringUtils.isNotEmpty(rawOutputStringBuilder.toString()) && StringUtils.isNotEmpty(
            fileContent)) {
          rawOutputStringBuilder.append('\n');
        }
        rawOutputStringBuilder.append(scriptFile.getFileContent());

      } catch (IOException | JcrFileReadException e) {
        LOG.error("Unable to append {} file {} to UiLibrary output due to IOException",
            scriptFile.getFileType().getFileModelClass().getName().replaceAll("[\r\n]", ""),
            scriptFile.getName().replaceAll("[\r\n]", ""));
      }
    }
    return rawOutputStringBuilder.toString();
  }

  @Nonnull
  @Override
  @Deprecated
  public String getUiLibrarySource(@Nonnull FrontendLibrary library, ScriptType scriptType) throws
      InvalidResourceTypeException, NoMatchingCompilerException {
    LOG.warn("getUiLibrarySource(FrontendLibrary, ScriptType) is deprecated.");
    ScriptTypeCompiler compiler = getCompiler(
        getLibraryScriptTypes(library, scriptType.getRootResourceName()), getCompilers());
    StringBuilder rawOutputStringBuilder = new StringBuilder();

    for (ScriptFile scriptFile : library.getScriptFiles(compiler.getScriptTypes(),
        scriptType.getRootResourceName())) {

      try {
        String fileContent = scriptFile.getFileContent();
        if (StringUtils.isNotEmpty(rawOutputStringBuilder.toString()) && StringUtils.isNotEmpty(
            fileContent)) {
          rawOutputStringBuilder.append('\n');
        }
        rawOutputStringBuilder.append(scriptFile.getFileContent());

      } catch (IOException | JcrFileReadException e) {
        LOG.error("Unable to append {} file {} to UiLibrary output due to IOException",
            scriptFile.getFileType().getFileModelClass().getName().replaceAll("[\r\n]", ""),
            scriptFile.getName().replaceAll("[\r\n]", ""));
      }
    }
    return rawOutputStringBuilder.toString();
  }

  @Nonnull
  @Override
  public List<ScriptType> getLibraryScriptTypes(FrontendLibrary library,
      @Nonnull String folderName) {
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
