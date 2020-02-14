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

/**
 * Folder Resource that contains CSS or JavaScript files, which are compiled into {@link UiLibrary}
 * instances.
 */
@Model(adaptables = Resource.class)
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
  public <T extends ScriptFile> List<T> getScriptFiles(final ScriptType scriptType) {
    final List<T> scriptFiles = new ArrayList<>();
    for (final String scriptPath : getIncludedScriptNames()) {
      try {
        scriptFiles.add(getChildAsFileType(scriptPath, this, scriptType.getFileModelClass()));
      } catch (final ChildResourceNotFoundException | InvalidResourceTypeException exception) {
        LOG.debug("Unable to retrieve {} script files for {}. {}", scriptType.getName(), getPath(),
            exception.getMessage());
      }
    }
    return scriptFiles;
  }

  @Nonnull
  protected List<BaseResource> getScriptFiles() {
    final List<BaseResource> scriptFiles = new ArrayList<>();
    for (final String scriptPath : getIncludedScriptNames()) {
      try {
        scriptFiles.add(getChildAsBaseResource(scriptPath, this));
      } catch (final ChildResourceNotFoundException exception) {
        LOG.debug("Unable to retrieve script file resource {} for {}. {}", scriptPath, getPath(),
            exception.getMessage());
      }
    }
    return scriptFiles;
  }
}
