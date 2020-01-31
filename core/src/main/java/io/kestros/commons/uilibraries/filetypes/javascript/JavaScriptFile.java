package io.kestros.commons.uilibraries.filetypes.javascript;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;

import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.uilibraries.filetypes.ScriptFile;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Model type for JavaScript files to be adapted to.
 */
@StructuredModel(validationService = JavaScriptFileValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class JavaScriptFile extends ScriptFile {

  @Override
  public FileType getFileType() {
    return JAVASCRIPT;
  }
}