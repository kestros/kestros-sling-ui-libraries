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

package io.kestros.commons.uilibraries.core.services.impl.sample;

import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.CssScriptTypeCompilerService;
import io.kestros.commons.uilibraries.api.services.ScriptTypeCompiler;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true,
        service = {CssScriptTypeCompilerService.class, ScriptTypeCompiler.class},
        property = "service.ranking:Integer=100")
public class SampleCompilerService implements ScriptTypeCompiler, CssScriptTypeCompilerService {
  @Nonnull
  @Override
  public List<ScriptType> getScriptTypes() {
    return Arrays.asList(ScriptTypes.CSS, SampleScriptType.SAMPLE_SCRIPT_TYPE);
  }

  @Nonnull
  @Override
  public String getOutput(@Nonnull String source) {
    return "COMPILED";
  }
}
