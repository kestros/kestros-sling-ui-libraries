package io.kestros.commons.uilibraries.eventlisteners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryCachePurgeEventListenerTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryCacheService uiLibraryCacheService;

  private UiLibraryCachePurgeEventListener eventListener;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    eventListener = context.registerInjectActivateService(new UiLibraryCachePurgeEventListener());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("ui-library-cache-purge", eventListener.getServiceUserName());
  }

  @Test
  public void testGetCacheService() {
    assertEquals(uiLibraryCacheService, eventListener.getCacheServices().get(0));
  }

  @Test
  public void testGetResourceResolverFactory() {
    assertNotNull(eventListener.getResourceResolverFactory());
  }
}