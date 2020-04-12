/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

  private final Map<String, Object> cssDirectoryProperties = new HashMap<>();
  private final Map<String, Object> fileProperties = new HashMap<>();
  private final Map<String, Object> cssContentProperties = new HashMap<>();

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

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());

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

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());
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
    final Resource cssFileResource = context.create().resource("/ui-library/css/css.css", fileProperties);

    final InputStream cssInputStream = new ByteArrayInputStream("body{ color:red;}".getBytes());
    cssContentProperties.put("jcr:data", cssInputStream);
    cssContentProperties.put("jcr:mimeType", "text/css");
    context.create().resource("/ui-library/css/css.css/jcr:content", cssContentProperties);

    final List<CssFile> cssFileList = new ArrayList<>();

    final CssFile mockCssFile = spy(new CssFile());
    doThrow(new IOException()).when(mockCssFile).getFileContent();
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