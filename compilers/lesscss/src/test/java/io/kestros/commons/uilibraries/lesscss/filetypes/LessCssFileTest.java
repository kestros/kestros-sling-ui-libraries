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

import static org.junit.Assert.*;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class LessCssFileTest {

  @Rule
  public SlingContext context = new SlingContext();

  private LessCssFile lessCssFile;
  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void getFileNameFromImport() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less").adaptTo(LessCssFile.class);
    assertEquals("test.less", lessCssFile.getFileNameFromImport("@import \"test.less\";"));
  }

  @Test
  public void isImportLine() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less").adaptTo(LessCssFile.class);
    assertTrue(lessCssFile.isImportLine("@import \"test.less\";"));
  }

  @Test
  public void getFileType() {
    context.create().resource("/content/test.less", "jcr:primaryType", "nt:file");
    lessCssFile = context.resourceResolver().getResource("/content/test.less").adaptTo(LessCssFile.class);
    assertEquals(LessCssScriptType.LESS, lessCssFile.getFileType());
  }

  @Test
  @Ignore
  public void getFileContent() {

  }
}