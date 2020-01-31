package io.kestros.commons.uilibraries.servlets;

import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component(immediate = true,
           service = Servlet.class,
           property = {"sling.servlet.resourceTypes=kestros/commons/ui-library",
               "sling.servlet.extensions=css", "sling.servlet.methods=GET",})
public class UiLibraryCssServlet extends BaseCssServlet {

  private static final long serialVersionUID = -1932666508054420750L;

  @Reference
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;

  @Override
  public Class<? extends UiLibrary> getUiLibraryClass() {
    return UiLibrary.class;
  }

  @Override
  protected UiLibraryConfigurationService getUiLibraryConfigurationService() {
    return uiLibraryConfigurationService;
  }

  @Override
  protected UiLibraryCacheService getUiLibraryCacheService() {
    return uiLibraryCacheService;
  }

}