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

package io.kestros.commons.uilibraries.lesscss.filetypes;


import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.structuredslingmodels.utils.FileModelUtils;
import io.kestros.commons.uilibraries.api.models.ScriptFile;
import java.io.BufferedReader;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling Model for .less files.
 */
@KestrosModel
@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class LessCssFile extends ScriptFile implements ScriptFile {

  private static final Logger LOG = LoggerFactory.getLogger(LessCssFile.class);

  @Override
  public FileType getFileType() {
    return LessCssScriptType.LESS;
  }

  @Override
  public String getFileContent() {
    final StringBuilder builder = new StringBuilder();

    final BufferedReader bufferedReader = getBufferedReader();

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
    } catch (final IOException exception) {
      LOG.error("Unable to build output for LessFile {} due to IOException", getPath());
    }
    return builder.toString();
  }

  private String getResolvedImportLine(String line) {
    final String filename = getFileNameFromImport(line);
    try {
      final BaseResource parentResource = getParent();

      final LessCssFile importedFile = FileModelUtils.getChildAsFileType(filename, parentResource,
          LessCssFile.class);

      line = importedFile.getFileContent();
    } catch (final ModelAdaptionException exception) {
      LOG.error("Unable to import Less script {} for {}. {}", filename, getPath(),
          exception.getMessage());
    }
    return line;
  }

  static String getFileNameFromImport(final String importLine) {
    final String[] importLineParts = importLine.split("@import \"");
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

  static boolean isImportLine(final String line) {
    if (line.contains("//")) {
      return false;
    }
    return StringUtils.isNotEmpty(getFileNameFromImport(line));
  }
}
