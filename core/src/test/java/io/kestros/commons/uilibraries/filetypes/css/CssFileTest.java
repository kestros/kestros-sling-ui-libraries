package io.kestros.commons.uilibraries.filetypes.css;

import static org.junit.Assert.assertEquals;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CssFileTest {

  @Rule
  public SlingContext context = new SlingContext();

  private CssFile cssFile;

  private Resource resource;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros.commons.uilibraries");
  }

  @Test
  public void testValidate() throws Exception {
    resource = context.create().resource("/file.css");
    cssFile = resource.adaptTo(CssFile.class);

    assertEquals(0, cssFile.getErrorMessages().size());
  }
}