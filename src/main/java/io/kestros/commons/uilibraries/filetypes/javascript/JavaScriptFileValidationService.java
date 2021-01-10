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

package io.kestros.commons.uilibraries.filetypes.javascript;

import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.uilibraries.filetypes.BaseScriptFileValidationService;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.validation.services.ModelValidatorRegistrationHandlerService;
import io.kestros.commons.validation.services.ModelValidatorRegistrationService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Validation Service for JavaScriptFile resources.
 */
@Component(immediate = true,
           service = ModelValidatorRegistrationService.class)
public class JavaScriptFileValidationService extends BaseScriptFileValidationService {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ModelValidatorRegistrationHandlerService modelValidatorRegistrationHandlerService;

  @Override
  public ModelValidatorRegistrationHandlerService getModelValidatorRegistrationHandlerService() {
    return modelValidatorRegistrationHandlerService;
  }

  @Override
  public FileType getFileType() {
    return ScriptType.JAVASCRIPT;
  }

  @Override
  public Class<? extends BaseSlingModel> getModelType() {
    return JavaScriptFile.class;
  }
}
