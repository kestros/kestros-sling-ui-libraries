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

package io.kestros.commons.uilibraries.basecompilers.filetypes.javascript;


import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.uilibraries.basecompilers.filetypes.BaseScriptFile;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Model type for JavaScript files to be adapted to.
 */
@KestrosModel
@Model(adaptables = Resource.class,
       resourceType = "nt:file")
public class JavaScriptFile extends BaseScriptFile {

  @Override
  public FileType getFileType() {
    return ScriptTypes.JAVASCRIPT;
  }
}