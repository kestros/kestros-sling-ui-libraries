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

package io.kestros.commons.uilibraries;

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.adaptToFileType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static io.kestros.commons.uilibraries.utils.UiLibraryUtils.getCssScriptTypes;
import static io.kestros.commons.uilibraries.utils.UiLibraryUtils.getScriptOutput;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptFile;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.filetypes.javascript.JavaScriptFile;
import io.kestros.commons.uilibraries.services.minification.UiLibraryMinificationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource Model used to proved css and js output.  `css` and `js` folders should exist as
 * children.
 */
@StructuredModel(validationService = UiLibraryValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = "kestros/commons/ui-library")
public class UiLibrary extends BaseResource {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibrary.class);

  private static final String PROPERTY_DEPENDENCIES = "dependencies";

  @OSGiService
  @Optional
  protected UiLibraryMinificationService uiLibraryMinificationService;

  /**
   * Absolute path to the rendered CSS script.
   *
   * @return Absolute path to the rendered CSS script.
   */
  public String getCssPath() {
    return getPath() + CSS.getExtension();
  }

  /**
   * Absolute path to the rendered JS script.
   *
   * @return Absolute path to the rendered JS script.
   */
  public String getJsPath() {
    return getPath() + JAVASCRIPT.getExtension();
  }

  /**
   * List of all scriptFiles for the specified ScriptType.
   *
   * @param scriptType ScriptType to retrieve.
   * @param <T> extends ScriptFile
   * @return List of all scriptFiles for the specified ScriptType.
   */
  public <T extends ScriptFile> List<T> getScriptFiles(final ScriptType scriptType) {
    if (scriptType.equals(JAVASCRIPT)) {
      return (List<T>) getJavaScriptScriptFiles();
    } else {
      return getCssScriptFiles();
    }
  }


  /**
   * Returns the uncached ScriptType output.
   *
   * @param scriptType ScriptType to retrieve.
   * @return The uncached ScriptType output.
   */
  public String getOutput(final ScriptType scriptType, final boolean minify)
      throws InvalidResourceTypeException {
    String output = getDependenciesOutput(scriptType);
    output += getScriptOutput(scriptType, this, false);

    if (minify) {
      if (uiLibraryMinificationService != null) {
        try {
          return uiLibraryMinificationService.getMinifiedOutput(output, scriptType);
        } catch (final ScriptCompressionException e) {
          LOG.warn("Unable to minify {} output for {}. {}", scriptType.getName(), getPath(),
              e.getMessage());
        }
      }
    }

    return output;
  }


  /**
   * All dependency UI Libraries.
   *
   * @return All dependency UI Libraries.
   */
  public List<UiLibrary> getDependencies() {
    final List<UiLibrary> dependencies = new ArrayList<>();

    for (final String dependencyPath : getDependencyPaths()) {
      try {
        final UiLibrary dependency = getResourceAsType(dependencyPath, getResourceResolver(),
            UiLibrary.class);
        dependencies.add(dependency);
      } catch (final InvalidResourceTypeException exception) {
        LOG.warn(
            "Unable to adapt dependency {} to UiLibrary for {} due to InvalidResourceTypeException",
            dependencyPath, getPath());
      } catch (final ResourceNotFoundException exception) {
        LOG.warn(
            "Unable to adapt dependency {} to UiLibrary for {} due to ResourceNotFoundException",
            dependencyPath, getPath());
      }
    }

    return dependencies;
  }

  /**
   * All supported scriptTypes ( from a maximum of two ScriptTypes, css/LESS/etc and javascript).
   *
   * @return All supported scriptTypes.
   */
  public List<ScriptType> getSupportedScriptTypes() {
    final List<ScriptType> supportedScriptTypes = new ArrayList<>();
    supportedScriptTypes.add(JAVASCRIPT);

    final List<ScriptType> supportableCssScriptTypes = new ArrayList<>();
    final List<ScriptType> unsupportedCssScriptTypes = new ArrayList<>();

    try {
      for (final BaseResource cssScriptResource : getCssScriptsFolder().getScriptFiles()) {
        for (final ScriptType cssScriptType : getCssScriptTypes()) {
          try {
            adaptToFileType(cssScriptResource, cssScriptType.getFileModelClass());
          } catch (final InvalidResourceTypeException exception) {
            unsupportedCssScriptTypes.add(cssScriptType);
          }
        }
      }
    } catch (final ChildResourceNotFoundException exception) {
      LOG.warn("Unable to determine supported css script files for {}. {}", getPath(),
          exception.getMessage());
    }

    for (final ScriptType scriptType : getCssScriptTypes()) {
      if (!unsupportedCssScriptTypes.contains(scriptType)) {
        supportableCssScriptTypes.add(scriptType);
      }
    }

    supportableCssScriptTypes.removeAll(unsupportedCssScriptTypes);

    if (supportableCssScriptTypes.size() == 1) {
      supportedScriptTypes.addAll(supportableCssScriptTypes);
    } else {
      supportedScriptTypes.add(CSS);
    }

    return supportedScriptTypes;
  }

  /**
   * All included css (or less, etc) files.
   *
   * @param <T> ScriptFile type
   * @return All included css files.
   */
  protected <T extends ScriptFile> List<T> getCssScriptFiles() {
    try {
      return getCssScriptsFolder().getScriptFiles(getCssScriptType());
    } catch (final ModelAdaptionException exception) {
      return Collections.emptyList();
    }
  }

  /**
   * All included javascript files.
   *
   * @return All included javascript files.
   */
  protected List<JavaScriptFile> getJavaScriptScriptFiles() {
    try {
      return getJavaScriptScriptsFolder().getScriptFiles(JAVASCRIPT);
    } catch (final ChildResourceNotFoundException exception) {
      return Collections.emptyList();
    }
  }

  protected UiLibraryScriptFolder getCssScriptsFolder() throws ChildResourceNotFoundException {
    return getScriptsRootResource(CSS, this).getResource().adaptTo(UiLibraryScriptFolder.class);
  }

  protected UiLibraryScriptFolder getJavaScriptScriptsFolder()
      throws ChildResourceNotFoundException {
    return getScriptsRootResource(JAVASCRIPT, this).getResource().adaptTo(
        UiLibraryScriptFolder.class);
  }

  /**
   * All dependency UI Library paths.
   *
   * @return All dependency UI Library paths.
   */
  List<String> getDependencyPaths() {
    return Arrays.asList(getProperties().get(PROPERTY_DEPENDENCIES, new String[]{}));
  }

  private static BaseResource getScriptsRootResource(final ScriptType scriptType,
      final UiLibrary uiLibrary) throws ChildResourceNotFoundException {
    return getChildAsBaseResource(scriptType.getRootResourceName(), uiLibrary);
  }

  private ScriptType getCssScriptType() throws InvalidResourceTypeException {
    if (getSupportedScriptTypes().size() == 2) {
      return getSupportedScriptTypes().get(1);
    }

    // TODO custom exception
    throw new InvalidResourceTypeException("", UiLibrary.class);
  }

  private String getDependenciesOutput(final ScriptType scriptType)
      throws InvalidResourceTypeException {
    final StringBuilder output = new StringBuilder();

    for (final UiLibrary dependency : getDependencies()) {
      output.append(dependency.getOutput(scriptType, false));
    }
    return output.toString();
  }

}