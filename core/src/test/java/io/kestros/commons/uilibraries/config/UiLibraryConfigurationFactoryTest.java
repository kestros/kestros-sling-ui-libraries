package io.kestros.commons.uilibraries.config;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.commons.uilibraries.config.UiLibraryConfigurationFactory.Config;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

public class UiLibraryConfigurationFactoryTest {

  private UiLibraryConfigurationFactory uiLibraryConfigurationFactory;

  private Config config;

  @Before
  public void setUp() throws Exception {
    config = mock(Config.class);
    uiLibraryConfigurationFactory = new UiLibraryConfigurationFactory();
  }

  @Test
  public void testActivate() throws Exception {
    when(config.minifiedLibraryPaths()).thenReturn(new String[]{});

    uiLibraryConfigurationFactory.activate(config, new HashMap<>());
  }

  @Test
  public void testGetMinifiedLibraryPaths() {
    when(config.minifiedLibraryPaths()).thenReturn(new String[]{"item-1", "item-2"});
    uiLibraryConfigurationFactory.activate(config, new HashMap<>());
    assertEquals(2, uiLibraryConfigurationFactory.getMinifiedLibraryPaths().size());
    assertEquals("item-1", uiLibraryConfigurationFactory.getMinifiedLibraryPaths().get(0));
    assertEquals("item-2", uiLibraryConfigurationFactory.getMinifiedLibraryPaths().get(1));
  }
}