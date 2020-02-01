package io.kestros.commons.uilibraries.filetypes.less;

import io.kestros.commons.uilibraries.filetypes.BaseScriptFileValidationService;

public class LessFileValidationService extends BaseScriptFileValidationService {

  @Override
  public LessFile getModel() {
    return (LessFile) getGenericModel();
  }

  @Override
  public void registerDetailedValidators() {
  }
}