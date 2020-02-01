package io.kestros.commons.uilibraries.filetypes.css;

import io.kestros.commons.uilibraries.filetypes.BaseScriptFileValidationService;

/**
 * Validator Service for CssFile models.
 */
public class CssFileValidationService extends BaseScriptFileValidationService {

  @Override
  public CssFile getModel() {
    return (CssFile) getGenericModel();
  }

  @Override
  public void registerDetailedValidators() {

  }
}