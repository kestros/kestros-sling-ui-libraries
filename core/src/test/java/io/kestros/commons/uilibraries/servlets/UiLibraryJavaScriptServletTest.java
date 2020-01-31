package io.kestros.commons.uilibraries.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryJavaScriptServletTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private UiLibraryCacheService uiLibraryCacheService;
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  private UiLibraryJavaScriptServlet servlet;

  private final Map<String, Object> properties = new HashMap<>();


  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    uiLibraryConfigurationService = mock(UiLibraryConfigurationService.class);
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    context.registerService(UiLibraryConfigurationService.class, uiLibraryConfigurationService);
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenReturn("output");
    servlet = spy(new UiLibraryJavaScriptServlet());
  }

  @Test
  public void testDoGet() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/ui-library");

    final Resource resource = context.create().resource("/ui-library", properties);

    context.request().setResource(resource);

    when(servlet.getUiLibraryCacheService()).thenReturn(uiLibraryCacheService);
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    servlet.doGet(context.request(), context.response());

    assertEquals(200, context.response().getStatus());
    assertEquals("application/javascript", context.response().getContentType());
    assertEquals("output", context.response().getOutputAsString());
  }


}