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

package io.kestros.commons.uilibraries.core;

import static org.junit.Assert.assertEquals;

import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiLibraryResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiLibraryResource uiLibrary;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> cssFolderProperties = new HashMap<>();
  private Map<String, Object> cssFileProperties = new HashMap<>();
  private Map<String, Object> jsFolderProperties = new HashMap<>();
  private Map<String, Object> jsFileProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    fileProperties.put("jcr:primaryType", "nt:file");
    cssFileProperties.put("jcr:primaryType", "nt:resource");
    cssFileProperties.put("jcr:mimeType", "text/css");
    jsFileProperties.put("jcr:primaryType", "nt:resource");
    jsFileProperties.put("jcr:mimeType", "application/javascript");
  }

  @Test
  public void testGetCssPath() {
    resource = context.create().resource("/ui-library", properties);
    uiLibrary = resource.adaptTo(UiLibraryResource.class);

    assertEquals("/ui-library.css", uiLibrary.getCssPath());
  }

  @Test
  public void testGetJsPath() {
    resource = context.create().resource("/ui-library", properties);
    uiLibrary = resource.adaptTo(UiLibraryResource.class);

    assertEquals("/ui-library.js", uiLibrary.getJsPath());
  }

  @Test
  public void testGetSupportedScriptTypes() {
  }

  @Test
  public void testGetScriptFilesCss() {
    resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});
    context.create().resource("/ui-library/css", cssFolderProperties);
    context.create().resource("/ui-library/css/file-1.css", fileProperties);
    context.create().resource("/ui-library/css/file-1.css/jcr:content", cssFileProperties);
    context.create().resource("/ui-library/css/file-2.css", fileProperties);
    context.create().resource("/ui-library/css/file-2.css/jcr:content", cssFileProperties);

    uiLibrary = resource.adaptTo(UiLibraryResource.class);

    assertEquals(2, uiLibrary.getScriptFiles(Collections.singletonList(ScriptTypes.CSS), "css").size());
    assertEquals("file-1.css", uiLibrary.getScriptFiles(Collections.singletonList(ScriptTypes.CSS), "css").get(0).getName());
    assertEquals("file-2.css", uiLibrary.getScriptFiles(Collections.singletonList(ScriptTypes.CSS), "css").get(1).getName());
  }

  @Test
  public void testGetScriptFilesWhenJavaScript() {
    resource = context.create().resource("/ui-library", properties);
    cssFolderProperties.put("include", new String[]{"file-1.js", "file-2.js"});
    context.create().resource("/ui-library/js", jsFolderProperties);
    context.create().resource("/ui-library/js/file-1.js", fileProperties);
    context.create().resource("/ui-library/js/file-1.js/jcr:content", jsFileProperties);
    context.create().resource("/ui-library/js/file-2.js", fileProperties);
    context.create().resource("/ui-library/js/file-2.js/jcr:content", jsFileProperties);

    uiLibrary = resource.adaptTo(UiLibraryResource.class);

    assertEquals(2, uiLibrary.getScriptFiles(Collections.singletonList(
        ScriptTypes.JAVASCRIPT), "js").size());
    assertEquals("file-1.js", uiLibrary.getScriptFiles(Collections.singletonList(ScriptTypes.JAVASCRIPT), "js").get(0).getName());
    assertEquals("file-2.js", uiLibrary.getScriptFiles(Collections.singletonList(ScriptTypes.JAVASCRIPT), "js").get(1).getName());

  }
}