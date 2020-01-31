package io.kestros.commons.uilibraries.filetypes.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CssFileValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private CssFileValidationService validationService;

  private CssFile cssFile;

  @Before
  public void setUp() throws Exception {
    cssFile = context.create().resource("/file.css").adaptTo(CssFile.class);
    validationService = spy(new CssFileValidationService());

    doReturn(cssFile).when(validationService).getGenericModel();
  }

  @Test
  public void testGetModel() {
    validationService.registerBasicValidators();
    assertEquals(cssFile, validationService.getModel());

  }

  @Test
  public void testRegisterBasicValidators() {
    validationService.registerBasicValidators();
    assertEquals(1, validationService.getBasicValidators().size());
    assertTrue(validationService.getBasicValidators().get(0).isValid());
    assertEquals("Resource name ends with .css extension.",
        validationService.getBasicValidators().get(0).getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.getBasicValidators().get(0).getType());
  }
}