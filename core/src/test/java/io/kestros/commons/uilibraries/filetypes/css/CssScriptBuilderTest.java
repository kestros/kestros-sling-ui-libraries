package io.kestros.commons.uilibraries.filetypes.css;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import io.kestros.commons.uilibraries.UiLibrary;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CssScriptBuilderTest {

  @Rule
  public SlingContext context = new SlingContext();

  private CssScriptBuilder cssScriptBuilder;

  private UiLibrary uiLibrary;

  private Resource resource;

  private Map<String, Object> cssDirectoryProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> cssContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros.commons.uilibraries");

    cssScriptBuilder = spy(new CssScriptBuilder());
  }

  @Test
  public void testGetOutput() throws Exception {
    resource = context.create().resource("/ui-library");

    cssDirectoryProperties.put("include", "css.css");
    context.create().resource("/ui-library/css", cssDirectoryProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/ui-library/css/css.css", fileProperties);

    InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

    cssContentProperties.put("jcr:data", cssInputStream);
    cssContentProperties.put("jcr:mimeType", "text/css");

    context.create().resource("/ui-library/css/css.css/jcr:content", cssContentProperties);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("body{ color:red;}\n", cssScriptBuilder.getOutput(uiLibrary));
    assertEquals(cssScriptBuilder.getUncompiledOutput(uiLibrary),
        cssScriptBuilder.getOutput(uiLibrary));
  }

  @Test
  public void testGetUncompiledOutput() throws Exception {
    resource = context.create().resource("/ui-library");

    cssDirectoryProperties.put("include", "css.css");
    context.create().resource("/ui-library/css", cssDirectoryProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/ui-library/css/css.css", fileProperties);

    InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());
    cssContentProperties.put("jcr:data", cssInputStream);
    cssContentProperties.put("jcr:mimeType", "text/css");
    context.create().resource("/ui-library/css/css.css/jcr:content", cssContentProperties);

    assertEquals("body{ color:red;}\n",
        cssScriptBuilder.getUncompiledOutput(resource.adaptTo(UiLibrary.class)));
  }

  @Test
  public void testGetUncompiledOutputWhenIoException() throws Exception {
    resource = context.create().resource("/ui-library");

    cssDirectoryProperties.put("include", "css.css");
    context.create().resource("/ui-library/css", cssDirectoryProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    Resource cssFileResource = context.create().resource("/ui-library/css/css.css", fileProperties);

    InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());
    cssContentProperties.put("jcr:data", cssInputStream);
    cssContentProperties.put("jcr:mimeType", "text/css");
    context.create().resource("/ui-library/css/css.css/jcr:content", cssContentProperties);

    List<CssFile> cssFileList = new ArrayList<>();

    CssFile mockCssFile = spy(new CssFile());
    doThrow(new IOException()).when(mockCssFile).getOutput();
    doReturn("mock-css-file.css").when(mockCssFile).getName();

    cssFileList.add(cssFileResource.adaptTo(CssFile.class));
    cssFileList.add(mockCssFile);

    doReturn(cssFileList).when(cssScriptBuilder).getFiles(uiLibrary);

    uiLibrary = resource.adaptTo(UiLibrary.class);

    assertEquals("body{ color:red;}\n", cssScriptBuilder.getUncompiledOutput(uiLibrary));
  }


  @Test
  public void testGetScriptsRootResource() throws Exception {
    resource = context.create().resource("/ui-library");
    context.create().resource("/ui-library/css");
    context.create().resource("/ui-library/css/css.css");

    assertEquals("/ui-library/css",
        cssScriptBuilder.getScriptsRootResource(CSS, resource.adaptTo(UiLibrary.class)).getPath());
  }

  @Test
  public void testGetFileTypeClass() throws Exception {
    resource = context.create().resource("/css.css");

    assertEquals("CssFile", cssScriptBuilder.getScriptType().getFileModelClass().getSimpleName());
  }

}