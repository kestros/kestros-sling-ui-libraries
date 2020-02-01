package io.kestros.commons.uilibraries.filetypes.javascript;

import io.kestros.commons.uilibraries.filetypes.BaseScriptFileValidationService;

/**
 * Validator Service for JavaScriptFile models.
 */
public class JavaScriptFileValidationService extends BaseScriptFileValidationService {

  @Override
  public JavaScriptFile getModel() {
    return (JavaScriptFile) getGenericModel();
  }

  @Override
  public void registerDetailedValidators() {
  }
}
