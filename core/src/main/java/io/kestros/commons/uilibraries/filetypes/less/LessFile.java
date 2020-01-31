package io.kestros.commons.uilibraries.filetypes.less;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.structuredslingmodels.utils.FileModelUtils;
import io.kestros.commons.uilibraries.filetypes.ScriptFile;
import java.io.BufferedReader;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StructuredModel(validationService = LessFileValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class LessFile extends ScriptFile {

  private static final Logger LOG = LoggerFactory.getLogger(LessFile.class);

  @Override
  public FileType getFileType() {
    return LESS;
  }

  @Override
  public String getOutput() {
    StringBuilder builder = new StringBuilder();

    BufferedReader bufferedReader = getBufferedReader();

    try {
      String line;
      boolean firstLine = true;
      while ((line = bufferedReader.readLine()) != null) {

        if (isImportLine(line)) {
          line = getResolvedImportLine(line);
        }
        if (!firstLine) {
          line = "\n" + line;
        }
        builder.append(line);

        firstLine = false;
      }
    } catch (IOException exception) {
      LOG.error("Unable to build output for LessFile {} due to IOException", getPath());
    }
    return builder.toString();
  }

  private String getResolvedImportLine(String line) {
    String filename = getFileNameFromImport(line);
    try {
      BaseResource parentResource = getParent();

      LessFile importedFile = FileModelUtils.getChildAsFileType(filename, parentResource,
          LessFile.class);

      line = importedFile.getOutput();
    } catch (ModelAdaptionException exception) {
      LOG.error("Unable to import Less script {} for {}. {}", filename, getPath(),
          exception.getMessage());
    }
    return line;
  }

  static String getFileNameFromImport(String importLine) {
    String[] importLineParts = importLine.split("@import \"");
    if (importLineParts.length == 2) {
      if (importLineParts[1].contains("\";")) {
        return importLineParts[1].split("\";")[0];
      } else {
        LOG.error("Syntax while getting file name from import line. {} is invalid.", importLine);
      }
    } else {
      LOG.trace("LESS line {} is not an import line.", importLine);
    }
    return StringUtils.EMPTY;
  }

  static boolean isImportLine(String line) {
    if (line.contains("//")) {
      return false;
    }
    return StringUtils.isNotEmpty(getFileNameFromImport(line));
  }
}
