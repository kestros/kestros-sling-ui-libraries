package io.kestros.commons.uilibraries.core.models;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.api.models.ScriptFileInterface;
import io.kestros.commons.uilibraries.api.models.ScriptTypeInterface;
import io.kestros.commons.uilibraries.api.models.UiLibraryInterface;
import io.kestros.commons.uilibraries.api.models.UiLibraryScriptFolderInterface;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UiLibraryResource extends BaseResource implements UiLibraryInterface {


  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryResource.class);

  private static final String PROPERTY_DEPENDENCIES = "dependencies";

  @OSGiService
  @Optional
  protected UiLibraryCompilationService uiLibraryCompilationService;

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
  public <T extends ScriptFileInterface> List<T> getScriptFiles(
      final ScriptTypeInterface scriptType) {
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
   * @param minify Whether to minify the output.
   * @return The uncached ScriptType output.
   * @throws InvalidResourceTypeException Thrown when a referenced dependency could not be
   *     adapted to UiLibrary.
   */
  public String getOutput(final ScriptTypeInterface scriptType, final boolean minify)
      throws InvalidResourceTypeException {
    if (uiLibraryCompilationService != null) {
      return uiLibraryCompilationService.getUiLibraryOutput(this, scriptType, minify);
    }
    return null;
  }

  /**
   * All dependency UI Libraries. Dependencies must be specified by complete resource path.
   *
   * @return All dependency UI Libraries.
   */
  public List<UiLibraryInterface> getDependencies() {
    final List<UiLibraryInterface> dependencies = new ArrayList<>();

    for (final String dependencyPath : getDependencyPaths()) {
      try {
        final UiLibraryInterface dependency = getResourceAsType(dependencyPath,
            getResourceResolver(), UiLibraryResource.class);
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
  public List<ScriptTypeInterface> getSupportedScriptTypes() {
    final List<ScriptType> supportedScriptTypes = new ArrayList<>();
    supportedScriptTypes.add(JAVASCRIPT);

    final List<ScriptType> supportableCssScriptTypes = new ArrayList<>();
    final List<ScriptType> unsupportedCssScriptTypes = new ArrayList<>();

    try {
      for (final BaseResource cssScriptResource : getCssScriptsFolder().getScriptFiles()) {
        for (final ScriptType cssScriptType : getCssScriptTypes()) {
          BaseFile file = cssScriptResource.getResource().adaptTo(
              cssScriptType.getFileModelClass());
          if (file != null) {
            if (!(cssScriptType.getReadableContentTypes().contains(file.getMimeType()))) {
              unsupportedCssScriptTypes.add(cssScriptType);
            } else if (!cssScriptType.getReadableExtensions().contains(file.getExtension())) {
              unsupportedCssScriptTypes.add(cssScriptType);
            }
          }
        }
      }
    } catch (final ChildResourceNotFoundException exception) {
      LOG.debug("Unable to determine supported css script files for {}. {}", getPath(),
          exception.getMessage());
    }

    for (final ScriptTypeInterface scriptType : getCssScriptTypes()) {
      if (!unsupportedCssScriptTypes.contains(scriptType)) {
        supportableCssScriptTypes.add(scriptType);
      }
    }

    supportableCssScriptTypes.removeAll(unsupportedCssScriptTypes);

    if (supportableCssScriptTypes.size() == 1) {
      supportedScriptTypes.addAll(supportableCssScriptTypes);
    } else if (supportableCssScriptTypes.size() == 2) {
      for (ScriptTypeInterface scriptType : supportableCssScriptTypes) {
        supportedScriptTypes.add(scriptType);
      }

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

  protected UiLibraryScriptFolderInterface getCssScriptsFolder()
      throws ChildResourceNotFoundException {
    return getScriptsRootResource(CSS, this).getResource().adaptTo(UiLibraryScriptFolder.class);
  }

  protected UiLibraryScriptFolderInterface getJavaScriptScriptsFolder()
      throws ChildResourceNotFoundException {
    return getScriptsRootResource(JAVASCRIPT, this).getResource().adaptTo(
        UiLibraryScriptFolder.class);
  }

  /**
   * All dependency UI Library paths.
   *
   * @return All dependency UI Library paths.
   */
  public List<String> getDependencyPaths() {
    return Arrays.asList(getProperties().get(PROPERTY_DEPENDENCIES, new String[]{}));
  }

  private static BaseResource getScriptsRootResource(final ScriptTypeInterface scriptType,
      final UiLibraryInterface uiLibrary) throws ChildResourceNotFoundException {
    UiLibraryResource uiLibraryResource = (UiLibraryResource) uiLibrary;
    return getChildAsBaseResource(scriptType.getRootResourceName(), uiLibraryResource);
  }

  private ScriptTypeInterface getCssScriptType() throws InvalidResourceTypeException {
    if (getSupportedScriptTypes().size() >= 2) {
      return getSupportedScriptTypes().get(1);
    }
    throw new InvalidResourceTypeException("", UiLibraryInterface.class);
  }
}
