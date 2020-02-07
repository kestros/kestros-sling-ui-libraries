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

/**
 * Baseline class used from building {@link ScriptType} output for {@link UiLibrary} Resources.
 */
public abstract class BaseScriptBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BaseScriptBuilder.class);

  /**
   * ScriptType associated to the ScriptBuilder implementation.
   *
   * @return ScriptType associated to the ScriptBuilder implementation.
   */
  public abstract ScriptType getScriptType();

  /**
   * Retrieves script output from a UiLibrary. ScriptType is defined by {@link #getScriptType()}
   *
   * @param uiLibrary UiLibrary to get output from.
   * @return Retrieves script output from a UiLibrary.
   */
  public abstract String getOutput(UiLibrary uiLibrary);

  /**
   * Retrieves a List of files that the ScriptBuilder will use to built the Output.
   *
   * @param uiLibrary UiLibrary to retrieve {@link ScriptFile} instances from.
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

  /**
   * Retrieves the root resource of the specified ScriptType.
   *
   * @param scriptType ScriptType
   * @param uiLibrary UiLibrary to retrieve root resource from.
   * @return The root resource of the specified ScriptType.
   * @throws ChildResourceNotFoundException Expected root resource does not exist.
   */
  public static BaseResource getScriptsRootResource(final ScriptType scriptType,
      final UiLibrary uiLibrary) throws ChildResourceNotFoundException {
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