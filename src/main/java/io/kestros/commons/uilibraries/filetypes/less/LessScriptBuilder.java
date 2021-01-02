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

package io.kestros.commons.uilibraries.filetypes.less;

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.adaptToFileType;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;

import com.inet.lib.less.Less;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptFile;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.filetypes.css.CssScriptBuilder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ScriptBuilder used for compiling LESS file(s) into CSS output.
 */
public class LessScriptBuilder extends CssScriptBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(LessScriptBuilder.class);

  @Override
  public ScriptType getScriptType() {
    return LESS;
  }

  @Override
  public String getOutput(final UiLibrary uiLibrary) {
    return Less.compile(null, getOutputWithResolvedImports(uiLibrary), false);
  }

  @Override
  public String getUncompiledOutput(final UiLibrary uiLibrary) {
    return getOutputWithResolvedImports(uiLibrary);
  }

  private String getOutputWithResolvedImports(final UiLibrary uiLibrary) {
    final StringBuilder builder = new StringBuilder();

    final List<ScriptFile> scriptFiles = getFiles(uiLibrary);
    for (final ScriptFile scriptFile : scriptFiles) {
      try {
        final LessFile lessFile = adaptToFileType(scriptFile, LessFile.class);
        builder.append(lessFile.getFileContent());
      } catch (final InvalidResourceTypeException e) {
        LOG.warn("Unable to add script file {} to uiLibrary {}. {}", scriptFile.getPath(),
            uiLibrary.getPath(), e.getMessage());
      }

    }

    return builder.toString();
  }
}