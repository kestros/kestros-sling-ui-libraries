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

package io.kestros.commons.uilibraries.core.filetypes;

import io.kestros.commons.structuredslingmodels.filetypes.BaseFile;
import io.kestros.commons.structuredslingmodels.filetypes.FileType;
import io.kestros.commons.uilibraries.api.models.ScriptBuilderInterface;
import io.kestros.commons.uilibraries.api.models.ScriptTypeInterface;
import io.kestros.commons.uilibraries.core.filetypes.css.CssFile;
import io.kestros.commons.uilibraries.core.filetypes.css.CssScriptBuilder;
import io.kestros.commons.uilibraries.core.filetypes.javascript.JavaScriptFile;
import io.kestros.commons.uilibraries.core.filetypes.javascript.JavaScriptScriptBuilder;
import io.kestros.commons.uilibraries.core.filetypes.less.LessFile;
import io.kestros.commons.uilibraries.core.filetypes.less.LessScriptBuilder;
import io.kestros.commons.uilibraries.core.models.BaseScriptBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Script {@link FileType} implementations used by UiLibraries for CSS and JavaScript output.
 */
public enum ScriptType implements ScriptTypeInterface, FileType {
  CSS("css", "css", Collections.singletonList("css"), "text/css",
      Collections.singletonList("text/css"), CssScriptBuilder.class, CssFile.class), JAVASCRIPT(
      "js", "js", Arrays.asList("js"), "application/javascript",
      Collections.singletonList("application/javascript"), JavaScriptScriptBuilder.class,
      JavaScriptFile.class), LESS("less", "css", Arrays.asList("css", "less"), "text/css",
      Arrays.asList("text/css", "text/less", "application/octet-stream"), LessScriptBuilder.class,
      LessFile.class);

  private final String name;
  private final String rootResourceName;
  private final String outputContentType;
  private final List<String> readableContentTypes;
  private final List<String> readableExtensions;
  private final String extension;
  private final Class<?> scriptFileType;
  private BaseScriptBuilder scriptBuilder;

  <T extends BaseScriptBuilder, S extends ScriptFile> ScriptType(final String name,
      final String rootResourceName, final List<String> readableExtensions,
      final String outputContentType, final List<String> readableContentTypes,
      final Class<T> scriptBuilderType, final Class<S> scriptFileType) {
    this.name = name;
    this.rootResourceName = rootResourceName;
    this.readableExtensions = readableExtensions;
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

  /**
   * Extensions that can be interpreted by this FileType and its associated Model.
   *
   * @return Extensions that can be interpreted by this FileType and its associated Model.
   */
  public List<String> getReadableExtensions() {
    return this.readableExtensions;
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
  public ScriptBuilderInterface getScriptBuilder() {
    return this.scriptBuilder;
  }

  @Nonnull
  @Override
  public <T extends BaseFile> Class<T> getFileModelClass() {
    return (Class<T>) this.scriptFileType;
  }
}