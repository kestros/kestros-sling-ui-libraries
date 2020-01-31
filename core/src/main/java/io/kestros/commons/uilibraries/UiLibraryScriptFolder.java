package io.kestros.commons.uilibraries;

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.getChildAsFileType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.filetypes.ScriptFile;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
// TODO add validation service.
public class UiLibraryScriptFolder extends BaseResource {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryScriptFolder.class);

  private static final String PROPERTY_INCLUDE = "include";

  /**
   * Filenames included script files.
   *
   * @return Filenames included script files.
   */
  @Nonnull
  public String[] getIncludedScriptNames() {
    return getProperty(PROPERTY_INCLUDE, new String[]{});
  }

  /**
   * List of all ScriptFile resources for the specified ScriptType.
   *
   * @param scriptType ScriptType to retrieve.
   * @param <T> extends ScriptType
   * @return List of all ScriptFile resources for the specified ScriptType.
   */
  public <T extends ScriptFile> List<T> getScriptFiles(ScriptType scriptType) {
    List<T> scriptFiles = new ArrayList<>();
    for (String scriptPath : getIncludedScriptNames()) {
      try {
        scriptFiles.add(getChildAsFileType(scriptPath, this, scriptType.getFileModelClass()));
      } catch (ChildResourceNotFoundException | InvalidResourceTypeException exception) {
        LOG.warn("Unable to retrieve {} script files for {}. {}", scriptType.getName(), getPath(),
            exception.getMessage());
      }
    }
    return scriptFiles;
  }

  @Nonnull
  protected List<BaseResource> getScriptFiles() {
    List<BaseResource> scriptFiles = new ArrayList<>();
    for (String scriptPath : getIncludedScriptNames()) {
      try {
        scriptFiles.add(getChildAsBaseResource(scriptPath, this));
      } catch (ChildResourceNotFoundException exception) {
        LOG.warn("Unable to retrieve script file resource {} for {}. {}", scriptPath, getPath(),
            exception.getMessage());
      }
    }
    return scriptFiles;
  }
}
