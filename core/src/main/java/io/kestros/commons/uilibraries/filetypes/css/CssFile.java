package io.kestros.commons.uilibraries.filetypes.css;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;

import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.uilibraries.filetypes.ScriptFile;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Model type for CSS files to be adapted to.
 */
@StructuredModel(validationService = CssFileValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class CssFile extends ScriptFile {

  @Override
  public FileType getFileType() {
    return CSS;
  }
}