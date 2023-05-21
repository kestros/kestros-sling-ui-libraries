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

import org.junit.Test;

public class LessCssScriptTypeTest {


  @Test
  public void getName() {
    assertEquals("less", LessCssScriptType.LESS.getName());
  }


  @Test
  public void getRootResourceName() {
    assertEquals("css", LessCssScriptType.LESS.getRootResourceName());
  }

  @Test
  public void getExtension() {
    assertEquals(".less", LessCssScriptType.LESS.getExtension());
  }

  @Test
  public void getReadableExtensions() {
    assertEquals("less", LessCssScriptType.LESS.getReadableExtensions().get(1));
  }

  @Test
  public void getOutputContentType() {
    assertEquals("text/css", LessCssScriptType.LESS.getOutputContentType());
  }

  @Test
  public void getReadableContentTypes() {
    assertEquals("text/less", LessCssScriptType.LESS.getReadableContentTypes().get(1));
  }

  @Test
  public void getFileModelClass() {
    assertEquals(LessCssFile.class, LessCssScriptType.LESS.getFileModelClass());
  }
}