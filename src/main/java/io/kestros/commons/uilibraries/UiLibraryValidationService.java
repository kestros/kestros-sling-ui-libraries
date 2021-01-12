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

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static io.kestros.commons.validation.ModelValidationMessageType.WARNING;
import static io.kestros.commons.validation.utils.CommonValidators.hasValidChild;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.BaseSlingModel;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.validation.ModelValidationMessageType;
import io.kestros.commons.validation.models.BaseModelValidationRegistrationService;
import io.kestros.commons.validation.models.ModelValidator;
import io.kestros.commons.validation.models.ModelValidatorBundle;
import io.kestros.commons.validation.services.ModelValidatorRegistrationHandlerService;
import io.kestros.commons.validation.services.ModelValidatorRegistrationService;
import java.util.ArrayList;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * ModelValidationService for validation {@link UiLibrary} models.
 */
@SuppressFBWarnings("RI_REDUNDANT_INTERFACES")
@Component(immediate = true,
           service = ModelValidatorRegistrationService.class)
public class UiLibraryValidationService extends BaseModelValidationRegistrationService
    implements ModelValidatorRegistrationService {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ModelValidatorRegistrationHandlerService modelValidatorRegistrationHandlerService;

  @Override
  public ModelValidatorRegistrationHandlerService getModelValidatorRegistrationHandlerService() {
    return modelValidatorRegistrationHandlerService;
  }

  @Override
  public Class<? extends BaseSlingModel> getModelType() {
    return UiLibrary.class;
  }

  @Override
  public List<ModelValidator> getModelValidators() {
    List<ModelValidator> uiLibraryValidators = new ArrayList<>();

    uiLibraryValidators.add(hasCssOrJsDirectory());

    return uiLibraryValidators;
  }

  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  ModelValidatorBundle hasCssOrJsDirectory() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        addValidator(hasValidChild(CSS.getName(), BaseResource.class));
        addValidator(hasValidChild(JAVASCRIPT.getName(), BaseResource.class));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return false;
      }

      @Override
      public String getMessage() {
        return "Has a valid CSS or JS directory.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.ERROR;
      }
    };
  }

  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  ModelValidator isAllIncludedScriptsFound(final ScriptType scriptType) {
    return new ModelValidator<UiLibrary>() {
      @Override
      public Boolean isValidCheck() {
        if (CSS.equals(scriptType)) {
          try {
            return getModel().getCssScriptFiles().size()
                   == getModel().getCssScriptsFolder().getIncludedScriptNames().size();
          } catch (final ChildResourceNotFoundException e) {
            return true;
          }
        }
        if (JAVASCRIPT.equals(scriptType)) {
          try {
            return getModel().getJavaScriptScriptFiles().size()
                   == getModel().getJavaScriptScriptsFolder().getIncludedScriptNames().size();
          } catch (final ChildResourceNotFoundException e) {
            return true;
          }
        }
        return false;
      }

      @Override
      public String getMessage() {
        return "All " + scriptType.getName() + " scripts exist and are valid.";
      }

      @Override
      public String getDetailedMessage() {
        return null;
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  protected ModelValidatorBundle isAllIncludedScriptsFound() {
    return new ModelValidatorBundle<UiLibrary>() {

      @Override
      public void registerValidators() {
        addValidator(isAllIncludedScriptsFound(CSS));
        addValidator(isAllIncludedScriptsFound(JAVASCRIPT));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public String getMessage() {
        return "All included CSS and Javascript files exist and are valid.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  protected ModelValidator isAllDependenciesFound() {
    return new ModelValidator<UiLibrary>() {
      @Override
      public Boolean isValidCheck() {
        return getModel().getDependencies().size() == getModel().getDependencyPaths().size();
      }

      @Override
      public String getMessage() {
        return "All dependencies exist and are valid.";
      }

      @Override
      public String getDetailedMessage() {
        return "";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

}
