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
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum ScriptType implements FileType {
  CSS("css", "css", "text/css", Arrays.asList("text/css"), CssScriptBuilder.class, CssFile.class),
  JAVASCRIPT("js", "js", "application/javascript", Arrays.asList("application/javascript"),
      JavaScriptScriptBuilder.class, JavaScriptFile.class), LESS("less", "css", "text/css",
      Arrays.asList("text/css", "text/less"), LessScriptBuilder.class, LessFile.class);

  private final String name;
  private final String rootResourceName;
  private final String outputContentType;
  private final List<String> readableContentTypes;
  private final String extension;
  private final Class scriptFileType;
  private BaseScriptBuilder scriptBuilder;

  <T extends BaseScriptBuilder, S extends ScriptFile> ScriptType(final String name,
      final String rootResourceName, final String outputContentType, final List<String> readableContentTypes,
      final Class<T> scriptBuilderType, final Class<S> scriptFileType) {
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
  public String getName() {
    return this.name;
  }

  @Nonnull
  public String getRootResourceName() {
    return this.rootResourceName;
  }

  @Nonnull
  public String getExtension() {
    return this.extension;
  }

  @Override
  public String getOutputContentType() {
    return this.outputContentType;
  }

  @Nonnull
  public List<String> getReadableContentTypes() {
    return this.readableContentTypes;
  }

  @Nullable
  public <T extends BaseScriptBuilder> T getScriptBuilder() {
    return (T) this.scriptBuilder;
  }

  @Nonnull
  public <T extends BaseFile> Class<T> getFileModelClass() {
    return this.scriptFileType;
  }
}