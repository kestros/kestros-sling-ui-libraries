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

package io.kestros.commons.uilibraries.filetypes;

import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.uilibraries.BaseScriptBuilder;
import io.kestros.commons.uilibraries.filetypes.css.CssFile;
import io.kestros.commons.uilibraries.filetypes.css.CssScriptBuilder;
import io.kestros.commons.uilibraries.filetypes.javascript.JavaScriptFile;
import io.kestros.commons.uilibraries.filetypes.javascript.JavaScriptScriptBuilder;
import io.kestros.commons.uilibraries.filetypes.less.LessFile;
import io.kestros.commons.uilibraries.filetypes.less.LessScriptBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Script {@link FileType} implementations used by UiLibraries for CSS and JavaScript output.
 */
public enum ScriptType implements FileType {
  CSS("css", "css", "text/css", Collections.singletonList("text/css"), CssScriptBuilder.class,
      CssFile.class), JAVASCRIPT("js", "js", "application/javascript",
      Collections.singletonList("application/javascript"), JavaScriptScriptBuilder.class,
      JavaScriptFile.class), LESS("less", "css", "text/css", Arrays.asList("text/css", "text/less"),
      LessScriptBuilder.class, LessFile.class);

  private final String name;
  private final String rootResourceName;
  private final String outputContentType;
  private final List<String> readableContentTypes;
  private final String extension;
  private final Class<?> scriptFileType;
  private BaseScriptBuilder scriptBuilder;

  <T extends BaseScriptBuilder, S extends ScriptFile> ScriptType(final String name,
      final String rootResourceName, final String outputContentType,
      final List<String> readableContentTypes, final Class<T> scriptBuilderType,
      final Class<S> scriptFileType) {
    this.name = name;
    this.rootResourceName = rootResourceName;
    this.outputContentType = outputContentType;
    this.readableContentTypes = readableContentTypes;
    this.extension = "." + name;
    this.scriptFileType = scriptFileType;

    try {
      this.scriptBuilder = scriptBuilderType.newInstance();
    } catch (final ReflectiveOperationException exception) {
      this.scriptBuilder = null;
    }
  }

  @Nonnull
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Resource folder name where scripts of this type should live.
   *
   * @return Resource folder name where scripts of this type should live.
   */
  @Nonnull
  public String getRootResourceName() {
    return this.rootResourceName;
  }

  @Nonnull
  @Override
  public String getExtension() {
    return this.extension;
  }

  @Override
  public String getOutputContentType() {
    return this.outputContentType;
  }

  @Nonnull
  @Override
  public List<String> getReadableContentTypes() {
    return this.readableContentTypes;
  }

  /**
   * {@link BaseScriptBuilder} to associate with a ScriptType.
   *
   * @param <T> Extends {@link BaseScriptBuilder}
   * @return {@link BaseScriptBuilder} to associate with a ScriptType
   */
  @Nonnull
  public <T extends BaseScriptBuilder> T getScriptBuilder() {
    return (T) this.scriptBuilder;
  }

  @Nonnull
  @Override
  public <T extends BaseFile> Class<T> getFileModelClass() {
    return (Class<T>) this.scriptFileType;
  }
}