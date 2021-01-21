package io.kestros.commons.uilibraries.api.models;

import java.util.List;

public interface UiLibraryInterface {


  String getCssPath();

  String getJsPath();

  List<UiLibraryInterface> getDependencies();

}
