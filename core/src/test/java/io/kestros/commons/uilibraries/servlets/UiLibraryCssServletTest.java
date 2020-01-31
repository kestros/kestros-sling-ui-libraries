package io.kestros.commons.uilibraries.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryCssServletTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private UiLibraryCacheService uiLibraryCacheService;
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  private UiLibraryCssServlet servlet;

  private final Map<String, Object> properties = new HashMap<>();

  private final Map<String, Object> cssFolderProperties = new HashMap<>();
  private final Map<String, Object> fileProperties = new HashMap<>();
  private final Map<String, Object> fileContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    uiLibraryConfigurationService = mock(UiLibraryConfigurationService.class);
    context.registerService(UiLibraryConfigurationService.class, uiLibraryConfigurationService);

    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenReturn("output");

    servlet = spy(new UiLibraryCssServlet());

    fileProperties.put("jcr:primaryType", "nt:file");

  }

  @Test
  public void testDoGet() throws Exception {
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);

    properties.put("sling:resourceType", "kestros/commons/ui-library");

    final Resource resource = context.create().resource("/ui-library", properties);

    context.request().setResource(resource);

    when(servlet.getUiLibraryCacheService()).thenReturn(uiLibraryCacheService);
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    servlet.doGet(context.request(), context.response());

    verify(uiLibraryCacheService, times(1)).getCachedOutput(any(UiLibrary.class), any(),
        anyBoolean());
    verify(uiLibraryCacheService, never()).cacheUiLibraryScripts(anyString(), anyBoolean());

    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("output", context.response().getOutputAsString());
  }

  @Test
  public void testDoGetWhenCacheBuilderException() throws Exception {
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);

    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenReturn("");
    doThrow(CacheBuilderException.class).when(uiLibraryCacheService).cacheUiLibraryScripts(
        anyString(), anyBoolean());

    properties.put("sling:resourceType", "kestros/commons/ui-library");

    final Resource resource = context.create().resource("/ui-library", properties);

    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    context.request().setResource(resource);

    when(servlet.getUiLibraryCacheService()).thenReturn(uiLibraryCacheService);
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    servlet.doGet(context.request(), context.response());

    verify(uiLibraryCacheService, times(1)).getCachedOutput(any(UiLibrary.class), any(),
        anyBoolean());
    verify(uiLibraryCacheService, never()).addCacheCreationJob(any());

    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("body{ color:red;}", context.response().getOutputAsString());
  }

  @Test
  public void testDoGetWhenNoValueIsCached() throws Exception {
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    properties.put("sling:resourceType", "kestros/commons/ui-library");

    final Resource resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    context.request().setResource(resource);

    when(servlet.getUiLibraryCacheService()).thenReturn(uiLibraryCacheService);
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);
    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenReturn("");

    servlet.doGet(context.request(), context.response());

    verify(uiLibraryCacheService, times(1)).getCachedOutput(any(), any(), anyBoolean());
    verify(uiLibraryCacheService, never()).addCacheCreationJob(any());

    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("body{ color:red;}", context.response().getOutputAsString());
  }

  @Test
  public void testDoGetWhenNoUiLibraryCacheService() throws Exception {
    properties.put("sling:resourceType", "kestros/commons/ui-library");

    final Resource resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);
    context.request().setResource(resource);

    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);
    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenReturn("");

    servlet.doGet(context.request(), context.response());

    verify(uiLibraryCacheService, never()).getCachedOutput(any(), any(), anyBoolean());
    verify(uiLibraryCacheService, never()).cacheUiLibraryScripts(anyString(), anyBoolean());

    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("body{ color:red;}", context.response().getOutputAsString());
  }

  @Test
  public void testDoGetWhenInvalidResourceType() throws Exception {
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    final Resource resource = context.create().resource("/ui-library", properties);
    context.request().setResource(resource);

    final UiLibraryCssServlet servlet = spy(new UiLibraryCssServlet());

    when(uiLibraryConfigurationService.getMinifiedLibraryPaths()).thenReturn(
        Collections.emptyList());

    servlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenCacheRetrievalException() throws Exception {
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenThrow(
        CacheRetrievalException.class);

    properties.put("sling:resourceType", "kestros/commons/ui-library");

    final Resource resource = context.create().resource("/ui-library", properties);

    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    context.request().setResource(resource);

    when(servlet.getUiLibraryCacheService()).thenReturn(uiLibraryCacheService);
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    servlet.doGet(context.request(), context.response());

    verify(uiLibraryCacheService, times(1)).getCachedOutput(any(UiLibrary.class), any(),
        anyBoolean());
    verify(uiLibraryCacheService, never()).addCacheCreationJob(any());

    assertEquals(200, context.response().getStatus());
    assertEquals("text/css", context.response().getContentType());
    assertEquals("body{ color:red;}", context.response().getOutputAsString());
  }

  @Test
  public void testDoGetWhenIoException() throws Exception {
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    when(uiLibraryCacheService.getCachedOutput(any(), any(), anyBoolean())).thenThrow(
        CacheRetrievalException.class);

    properties.put("sling:resourceType", "kestros/commons/ui-library");

    final Resource resource = context.create().resource("/ui-library", properties);

    cssFolderProperties.put("include", new String[]{"file.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    fileContentProperties.put("jcr:data", cssInputStream);
    fileContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/file.css/jcr:content", fileContentProperties);

    context.request().setResource(resource);

    when(servlet.getUiLibraryCacheService()).thenReturn(uiLibraryCacheService);
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    doThrow(IOException.class).when(servlet).write(any(), any());

    servlet.doGet(context.request(), context.response());

    verify(uiLibraryCacheService, times(1)).getCachedOutput(any(UiLibrary.class), any(),
        anyBoolean());
    verify(uiLibraryCacheService, never()).addCacheCreationJob(any());

    assertEquals(400, context.response().getStatus());
    assertEquals("text/plain", context.response().getContentType());
    assertEquals("", context.response().getOutputAsString());
  }

  @Test
  public void testIsMinified() {
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    final List<String> minifiedPaths = new ArrayList<>();
    minifiedPaths.add("/ui-library");
    when(uiLibraryConfigurationService.getMinifiedLibraryPaths()).thenReturn(minifiedPaths);

    final Resource resource = context.create().resource("/ui-library", properties);
    context.request().setResource(resource);

    assertTrue(servlet.isMinified(context.request()));
  }

  @Test
  public void testIsMinifiedWhenLibraryIsNotMinified() {
    when(servlet.getUiLibraryConfigurationService()).thenReturn(uiLibraryConfigurationService);

    final List<String> minifiedPaths = new ArrayList<>();
    when(uiLibraryConfigurationService.getMinifiedLibraryPaths()).thenReturn(minifiedPaths);

    final Resource resource = context.create().resource("/ui-library", properties);
    context.request().setResource(resource);

    assertFalse(servlet.isMinified(context.request()));
  }

  @Test
  public void testIsMinifiedWhenLibraryIsConfigurationServiceIsNull() {
    final Resource resource = context.create().resource("/ui-library", properties);
    context.request().setResource(resource);

    assertFalse(servlet.isMinified(context.request()));
  }
}