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

package io.kestros.commons.uilibraries.filetypes;

import static io.kestros.commons.validation.utils.CommonValidators.hasFileExtension;

import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.validation.ModelValidationMessageType;
import io.kestros.commons.validation.models.ModelValidator;
import io.kestros.commons.validation.services.ModelValidatorRegistrationService;
import java.util.ArrayList;
import java.util.List;

/**
 * Baseline validation for {@link ScriptType} implementations. Checks that nt:file Resources have
 * the proper extension before considering them valid.
 */
public abstract class BaseScriptFileValidationService implements ModelValidatorRegistrationService {

  /**
   * FileType to validate model against.
   *
   * @return FileType to validate model against.
   */
  public abstract FileType getFileType();

  @Override
  public List<ModelValidator> getModelValidators() {
    List<ModelValidator> modelValidators = new ArrayList<>();
    modelValidators.add(
        hasFileExtension(getFileType().getExtension(), ModelValidationMessageType.ERROR));
    return modelValidators;
  }

}