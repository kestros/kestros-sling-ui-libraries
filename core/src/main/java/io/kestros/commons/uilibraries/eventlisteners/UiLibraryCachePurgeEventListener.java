package io.kestros.commons.uilibraries.eventlisteners;

import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getAllOsgiServicesOfType;

import io.kestros.commons.osgiserviceutils.services.eventlisteners.impl.BaseCachePurgeOnResourceChangeEventListener;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ResourceChangeListener.class,
           property = {ResourceChangeListener.CHANGES + "=ADDED",
               ResourceChangeListener.CHANGES + "=CHANGED",
               ResourceChangeListener.CHANGES + "=REMOVED",
               ResourceChangeListener.CHANGES + "=PROVIDER_ADDED",
               ResourceChangeListener.CHANGES + "=PROVIDER_REMOVED",
               ResourceChangeListener.PATHS + "=/apps", ResourceChangeListener.PATHS + "=/etc",
               ResourceChangeListener.PATHS + "=/libs"},
           immediate = true)
public class UiLibraryCachePurgeEventListener extends BaseCachePurgeOnResourceChangeEventListener {

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  protected String getServiceUserName() {
    return "ui-library-cache-purge";
  }

  @Override
  public List<UiLibraryCacheService> getCacheServices() {
    return getAllOsgiServicesOfType(getComponentContext(), UiLibraryCacheService.class);
  }

  @Override
  public ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }
}
