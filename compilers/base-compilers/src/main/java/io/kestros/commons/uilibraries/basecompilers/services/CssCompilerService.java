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

package io.kestros.commons.uilibraries.basecompilers.services;

import static io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptType.CSS;

import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.CssScriptTypeCompilerService;
import io.kestros.commons.uilibraries.api.services.ScriptTypeCompiler;
import java.util.Collections;
import java.util.List;
import org.osgi.service.component.annotations.Component;

/**
 * Compiles CSS by simply passing back the source value.
 */
@Component(immediate = true,
           service = {CssScriptTypeCompilerService.class, ScriptTypeCompiler.class})
public class CssCompilerService implements ScriptTypeCompiler, CssScriptTypeCompilerService {

  @Override
  public List<ScriptType> getScriptTypes() {
    return Collections.singletonList(CSS);
  }

  @Override
  public String getOutput(String source) {
    return source;
  }
}
