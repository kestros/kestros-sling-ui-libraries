package io.kestros.commons.uilibraries;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.uilibraries.filetypes.ScriptFile;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseScriptBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BaseScriptBuilder.class);

  public abstract ScriptType getScriptType();

  public abstract String getOutput(UiLibrary uiLibrary);

  /**
   * Retrieves a List of files that the ScriptBuilder will use to built the Output.
   *
   * @param <T> Class which extends BaseFile
   * @return All files that will be used to build the script output.
   */
  @Nonnull
  public <T extends ScriptFile> List<T> getFiles(final UiLibrary uiLibrary) {
    return uiLibrary.getScriptFiles(getScriptType());
  }

  /**
   * A UiLibrary's uncompiled/raw output for the associated ScriptType.
   *
   * @param uiLibrary UiLibrary to retrieve output of.
   * @param <T> extends ScriptFile
   * @return A UiLibrary's uncompiled/raw output for the associated ScriptType.
   */
  public <T extends ScriptFile> String getUncompiledOutput(final UiLibrary uiLibrary) {
    final StringBuilder scriptStringBuilder = new StringBuilder();
    final List<T> files = getFiles(uiLibrary);

    for (final T file : files) {
      appendFileOutputToStringBuilder(file, scriptStringBuilder);
    }

    return scriptStringBuilder.toString();
  }

  public static BaseResource getScriptsRootResource(final ScriptType scriptType, final UiLibrary uiLibrary)
      throws ChildResourceNotFoundException {
    return getChildAsBaseResource(scriptType.getRootResourceName(), uiLibrary);
  }


  private <T extends ScriptFile> void appendFileOutputToStringBuilder(final T file,
      final StringBuilder stringBuilder) {
    try {
      stringBuilder.append(file.getOutput());
      stringBuilder.append("\n");
    } catch (final IOException exception) {
      LOG.error("Unable to append {} file {} to UiLibrary output due to IOException",
          file.getFileType().getFileModelClass(), file.getName());
    }
  }

}