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

package io.kestros.commons.uilibraries.utils;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;

import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class UiLibraryUtils {


  private UiLibraryUtils() {
  }

  public static List<ScriptType> getCssScriptTypes() {
    return Arrays.asList(CSS, LESS);
  }

  /**
   * Return the script output for the specified ScriptType.
   *
   * @param scriptType The script type to retrieve.
   * @param uiLibrary The UI Library to retrieve the script from.
   * @param compiled whether or not to compile the script, if css.
   * @param <T> Class that extends UiLibrary
   * @return The script output for the specified ScriptType.
   */
  @Nonnull
  public static <T extends UiLibrary> String getScriptOutput(@Nonnull final ScriptType scriptType,
      @Nonnull final T uiLibrary, final boolean compiled) {
    if (UiLibraryUtils.getCssScriptTypes().contains(scriptType)) {

      String cssOutput = getOutput(uiLibrary, CSS);
      final String lessOutput;

      if (compiled) {
        lessOutput = getOutput(uiLibrary, LESS);
      } else {
        lessOutput = LESS.getScriptBuilder().getUncompiledOutput(uiLibrary);
      }

      if (StringUtils.isNotEmpty(lessOutput)) {
        cssOutput = lessOutput;
      }

      if (StringUtils.isNotEmpty(cssOutput)) {
        return cssOutput;
      }

      return StringUtils.EMPTY;
    } else if (JAVASCRIPT.getName().equals(scriptType.getName())) {
      return getOutput(uiLibrary, JAVASCRIPT);
    }
    throw new IllegalStateException();
  }


  private static <T extends UiLibrary> String getOutput(@Nonnull final T uiLibrary,
      @Nonnull final ScriptType scriptType) {
    return scriptType.getScriptBuilder().getOutput(uiLibrary);
  }

}