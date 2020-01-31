package io.kestros.commons.uilibraries.filetypes;

import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasFileExtension;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;

public abstract class BaseScriptFileValidationService extends ModelValidationService {

  @Override
  public void registerBasicValidators() {
    final ScriptFile model = getModel();
    addBasicValidator(hasFileExtension(model.getFileType().getExtension(), model, ERROR));
  }

  @Override
  public void registerDetailedValidators() {
  }
}