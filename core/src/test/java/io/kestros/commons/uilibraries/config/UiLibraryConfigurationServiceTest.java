package io.kestros.commons.uilibraries.config;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.config.UiLibraryConfigurationFactory.Config;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

public class UiLibraryConfigurationServiceTest {


  private UiLibraryConfigurationService configurationService;

  private UiLibraryConfigurationFactory uiLibraryConfigurationFactory1;
  private UiLibraryConfigurationFactory uiLibraryConfigurationFactory2;

  private Config config1 = mock(Config.class);
  private Config config2 = mock(Config.class);

  @Before
  public void setUp() throws Exception {
    configurationService = new UiLibraryConfigurationService();

    uiLibraryConfigurationFactory1 = spy(new UiLibraryConfigurationFactory());
    uiLibraryConfigurationFactory2 = spy(new UiLibraryConfigurationFactory());

    when(config1.minifiedLibraryPaths()).thenReturn(new String[]{"item-1"});
    when(config2.minifiedLibraryPaths()).thenReturn(new String[]{"item-2"});

    uiLibraryConfigurationFactory1.activate(config1, new HashMap<>());
    uiLibraryConfigurationFactory2.activate(config2, new HashMap<>());
  }

  @Test
  public void testBindUiLibraryConfigurationServiceFactory() throws Exception {
    assertEquals(0, configurationService.getMinifiedLibraryPaths().size());

    configurationService.bindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory1);
    assertEquals(1, configurationService.getMinifiedLibraryPaths().size());

    configurationService.bindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory2);
    assertEquals(2, configurationService.getMinifiedLibraryPaths().size());
  }

  @Test
  public void testUnbindUiLibraryConfigurationServiceFactory() throws Exception {
    assertEquals(0, configurationService.getMinifiedLibraryPaths().size());

    configurationService.bindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory1);
    configurationService.bindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory2);
    assertEquals(2, configurationService.getMinifiedLibraryPaths().size());

    configurationService.unbindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory1);
    configurationService.unbindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory2);
    assertEquals(0, configurationService.getMinifiedLibraryPaths().size());
  }

  @Test
  public void testGetMinifiedLibraryPaths() throws Exception {
    configurationService.bindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory1);
    configurationService.bindUiLibraryConfigurationServiceFactory(uiLibraryConfigurationFactory2);

    assertEquals(2, configurationService.getMinifiedLibraryPaths().size());
    assertEquals("item-1", configurationService.getMinifiedLibraryPaths().get(0));
    assertEquals("item-2", configurationService.getMinifiedLibraryPaths().get(1));
  }
}