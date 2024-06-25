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

package io.kestros.commons.uilibraries.core;

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.getChildAsFileType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.models.ScriptFile;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.models.UiLibrary;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling Model for {@link UiLibrary} resources.
 */
@Model(adaptables = Resource.class,
       resourceType = "kes:UiLibrary")
public class UiLibraryResource extends BaseResource implements UiLibrary {

  private static final Logger LOG = LoggerFactory.getLogger(UiLibraryResource.class);

  @Nonnull
  @Override
  public String getCssPath() {
    return getPath() + ScriptTypes.CSS.getExtension();
  }

  @Nonnull
  @Override
  public String getJsPath() {
    return getPath() + ScriptTypes.JAVASCRIPT.getExtension();
  }

  @Nonnull
  @Override
  public List<String> getIncludedFileNames(ScriptType scriptType) {
    try {
      BaseResource folderResource = getChildAsBaseResource(scriptType.getName(),
          this);
      return Arrays.asList(folderResource.getProperty("include", new String[]{}));
    } catch (ChildResourceNotFoundException e) {
      LOG.debug("Failed to find folder resource {} for {}.",
              scriptType.getName().replaceAll("[\r\n]", ""), getPath().replaceAll("[\r\n]", ""));
    }
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public <T extends ScriptFile> List<T> getScriptFiles(@Nonnull List<ScriptType> scriptTypes,
      @Nonnull String folderName) {
    List<T> scriptFileList = new ArrayList<>();

    BaseResource folderResource = null;
    try {
      folderResource = getChildAsBaseResource(folderName, this);
      for (String includedFileName : getIncludedFileNames(ScriptTypes.lookup(folderName))) {
        for (ScriptType scriptType : scriptTypes) {

          try {
            T script = (T) getChildAsFileType(includedFileName, folderResource,
                scriptType.getFileModelClass());
            scriptFileList.add(script);
          } catch (ChildResourceNotFoundException e) {
            LOG.trace(e.getMessage().replaceAll("[\r\n]", ""));
          } catch (InvalidResourceTypeException e) {
            LOG.trace(e.getMessage().replaceAll("[\r\n]", ""));
          }
        }
      }
    } catch (ChildResourceNotFoundException e) {
      LOG.debug(e.getMessage().replaceAll("[\r\n]", ""));
    }
    return scriptFileList;
  }
}