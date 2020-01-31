package io.kestros.commons.uilibraries;

import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasDescription;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasTitle;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasValidChild;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;

import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidatorBundle;
import io.kestros.commons.uilibraries.filetypes.ScriptType;

public class UiLibraryValidationService extends ModelValidationService {

  @Override
  public UiLibrary getModel() {
    return (UiLibrary) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    addBasicValidator(hasTitle(getModel()));
    addBasicValidator(hasDescription(getModel(), WARNING));
    addBasicValidator(hasCssOrJsDirectory());
    addBasicValidator(isAllIncludedScriptsFound());
    addBasicValidator(isAllDependenciesFound());
  }

  @Override
  public void registerDetailedValidators() {
  }

  ModelValidatorBundle hasCssOrJsDirectory() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        addBasicValidator(hasValidChild(CSS.getName(), BaseResource.class, getModel()));
        addBasicValidator(hasValidChild(JAVASCRIPT.getName(), BaseResource.class, getModel()));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return false;
      }

      @Override
      public String getBundleMessage() {
        return "Has a valid CSS or JS directory.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  ModelValidator isAllIncludedScriptsFound(final ScriptType scriptType) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        if (CSS.equals(scriptType)) {
          try {
            return getModel().getCssScriptFiles().size()
                   == getModel().getCssScriptsFolder().getIncludedScriptNames().length;
          } catch (final ChildResourceNotFoundException e) {
            return true;
          }
        }
        if (JAVASCRIPT.equals(scriptType)) {
          try {
            return getModel().getJavaScriptScriptFiles().size()
                   == getModel().getJavaScriptScriptsFolder().getIncludedScriptNames().length;
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
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  ModelValidatorBundle isAllIncludedScriptsFound() {
    return new ModelValidatorBundle() {

      @Override
      public String getBundleMessage() {
        return "All included CSS and Javascript files exist and are valid.";
      }

      @Override
      public void registerValidators() {
        addBasicValidator(isAllIncludedScriptsFound(CSS));
        addBasicValidator(isAllIncludedScriptsFound(JAVASCRIPT));
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  ModelValidator isAllDependenciesFound() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return getModel().getDependencies().size() == getModel().getDependencyPaths().size();
      }

      @Override
      public String getMessage() {
        return "All dependencies exist and are valid.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }
}
