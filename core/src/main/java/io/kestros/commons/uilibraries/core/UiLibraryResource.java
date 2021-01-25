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

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.getChildrenOfFileType;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import io.kestros.commons.uilibraries.api.models.ScriptFile;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.models.UiLibrary;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.util.ArrayList;
import java.util.List;
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

  @Override
  public String getCssPath() {
    return getPath() + ScriptTypes.CSS.getExtension();
  }

  @Override
  public String getJsPath() {
    return getPath() + ScriptTypes.JAVASCRIPT.getExtension();
  }

  @Override
  public <T extends ScriptFile> List<T> getScriptFiles(
      List<ScriptType> scriptTypes, String folderName) {
    List<T> scriptFileList = new ArrayList<>();

    BaseResource folder = null;
    try {

      if (ScriptTypes.CSS.getRootResourceName().equals(folderName)) {
        folder = SlingModelUtils.getChildAsBaseResource(
            ScriptTypes.CSS.getName(), this);
      } else if (ScriptTypes.JAVASCRIPT.getRootResourceName().equals(folderName)) {
        folder = SlingModelUtils.getChildAsBaseResource(
            ScriptTypes.JAVASCRIPT.getName(), this);
      }
    } catch (ChildResourceNotFoundException e) {
      LOG.debug(String.format("Skipping %s retrieval for %s. No folder detected.", folderName,
          getPath()));
    }
    if (folder != null) {
      for (ScriptType scriptType : scriptTypes) {
        for (BaseFile file : getChildrenOfFileType(folder, scriptType.getFileModelClass())) {
          scriptFileList.add((T) file);
        }
      }
    } else {
      LOG.debug(String.format("Skipping %s retrieval for %s. No folder detected.", folderName,
          getPath()));
    }
    return scriptFileList;
  }
}