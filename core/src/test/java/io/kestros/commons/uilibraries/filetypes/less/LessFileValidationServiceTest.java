package io.kestros.commons.uilibraries.filetypes.less;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LessFileValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private LessFileValidationService validationService;

  private LessFile lessFile;

  @Before
  public void setUp() throws Exception {
    lessFile = context.create().resource("/file.less").adaptTo(LessFile.class);
    validationService = spy(new LessFileValidationService());

    doReturn(lessFile).when(validationService).getGenericModel();
  }

  @Test
  public void testGetModel() {
    validationService.registerBasicValidators();
    assertEquals(lessFile, validationService.getModel());
  }

  @Test
  public void testRegisterBasicValidators() {
    validationService.registerBasicValidators();
    assertEquals(1, validationService.getBasicValidators().size());
    assertTrue(validationService.getBasicValidators().get(0).isValid());
    assertEquals("Resource name ends with .less extension.",
        validationService.getBasicValidators().get(0).getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.getBasicValidators().get(0).getType());
  }

}