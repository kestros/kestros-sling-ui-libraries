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

package io.kestros.commons.uilibraries.lesscss.filetypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.exceptions.JcrFileReadException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LessCssFileTest {

  @Rule
  public SlingContext context = new SlingContext();

  private LessCssFile lessCssFile;
  private Map<String, Object> jcrContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void getFileNameFromImport() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less")
            .adaptTo(LessCssFile.class);
    assertEquals("test.less", lessCssFile.getFileNameFromImport("@import \"test.less\";"));
  }

  @Test
  public void isImportLine() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less")
            .adaptTo(LessCssFile.class);
    assertTrue(lessCssFile.isImportLine("@import \"test.less\";"));
  }

  @Test
  public void getFileType() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less")
            .adaptTo(LessCssFile.class);
    assertEquals(LessCssScriptType.LESS, lessCssFile.getFileType());
  }

  @Test
  public void getFileContent() throws IOException, JcrFileReadException {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    File testLessFile = new File(getClass().getResource("/test.less").getFile());
    String contents = FileUtils.readFileToString(testLessFile, "UTF-8");
    InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
    jcrContentProperties.put("jcr:data", inputStream);
    context.create().resource("/content/test.less/jcr:content", jcrContentProperties);
    lessCssFile = spy(context.resourceResolver().getResource("/content/test.less")
            .adaptTo(LessCssFile.class));


    assertEquals(".body {\n"
            + "  color: #000;\n"
            + "  background-color: #fff;\n"
            + "}", lessCssFile.getFileContent());
  }

  @Test
  public void testGetResolvedImportLine() throws IOException, JcrFileReadException {
    context.create().resource("/content/imported.less", "jcr:primaryType", "nt:file");
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");

    File testLessFile = new File(getClass().getResource("/test-with-imports.less").getFile());
    String contents = FileUtils.readFileToString(testLessFile, "UTF-8");
    InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
    jcrContentProperties.put("jcr:data", inputStream);
    context.create().resource("/content/test.less/jcr:content", jcrContentProperties);

    File importedLessFile = new File(getClass().getResource("/imported.less").getFile());
    String importedContents = FileUtils.readFileToString(importedLessFile, "UTF-8");
    InputStream importedInputStream = new ByteArrayInputStream(importedContents.getBytes());
    jcrContentProperties.put("jcr:data", importedInputStream);
    jcrContentProperties.put("jcr:mimeType", "text/less");
    context.create().resource("/content/imported.less/jcr:content", jcrContentProperties);


    lessCssFile = spy(context.resourceResolver().getResource("/content/test.less")
            .adaptTo(LessCssFile.class));

    assertEquals("// import\n"
            + ".imported {\n"
            + "  color: #000;\n"
            + "  background-color: #fff;\n"
            + "}\n"
            + "\n"
            + ".body {\n"
            + "  color: #000;\n"
            + "  background-color: #fff;\n"
            + "}", lessCssFile.getFileContent());
  }

  @Test
  public void testGetResolvedImportLineTestWhenJcrFileReadException() throws IOException,
          JcrFileReadException {
    context.create().resource("/content/imported.less", "jcr:primaryType", "nt:file");
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");

    File testLessFile = new File(getClass().getResource("/test-with-imports.less").getFile());
    String contents = FileUtils.readFileToString(testLessFile, "UTF-8");
    InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
//    jcrContentProperties.put("jcr:data", inputStream);
    context.create().resource("/content/test.less/jcr:content", jcrContentProperties);

    File importedLessFile = new File(getClass().getResource("/imported.less").getFile());
    String importedContents = FileUtils.readFileToString(importedLessFile, "UTF-8");
    InputStream importedInputStream = new ByteArrayInputStream(importedContents.getBytes());
    jcrContentProperties.put("jcr:data", importedInputStream);
    jcrContentProperties.put("jcr:mimeType", "text/less");
    context.create().resource("/content/imported.less/jcr:content", jcrContentProperties);


    lessCssFile = spy(context.resourceResolver().getResource("/content/test.less")
            .adaptTo(LessCssFile.class));

    assertEquals("@import \"test.less\";",
            lessCssFile.getResolvedImportLine("@import \"test.less\";"));
}




}