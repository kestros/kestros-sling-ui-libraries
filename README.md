# Kestros Sling UI Libraries

Core implementation of the Kestros UI library compilation system, providing CSS and JavaScript compilation, caching, and minification for Sling resources.

## Purpose

`kestros-sling-ui-libraries` implements the service interfaces defined in `kestros-sling-ui-libraries-api`. It provides the runtime infrastructure for compiling, caching, and serving CSS and JavaScript from UI library resources stored in the JCR.

This is a **reactor project** with three sub-modules:

| Module | Purpose |
|--------|---------|
| `core` | Core UI library model, compilation orchestration, caching, and configuration services |
| `compilers` | Script type compilers (e.g., LessCSS to CSS) |
| `minifiers` | CSS and JavaScript minification services |

## Installation & Build

**Maven coordinates:**

```
io.kestros.commons:kestros-sling-ui-libraries
```

**Build all modules:**

```bash
mvn clean package
```

**Deploy to a Sling instance:**

```bash
mvn clean install -P installBundle -Dsling.host=localhost -Dsling.port=8080
```

## Configuration

### Service User Mapping

Services in this module require a service user mapping for JCR access to UI library resources.

### Creating a UI Library

UI libraries are JCR resources with type `kes:UiLibrary`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0"
  xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:kes="http://kestros.io/kes/1.0"
  jcr:primaryType="kes:UiLibrary"
  jcr:title="My UI Library"/>
```

CSS and JavaScript scripts are organized in child folders with an `include` property specifying compilation order:

```xml
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0"
  xmlns:jcr="http://www.jcp.org/jcr/1.0"
  jcr:primaryType="sling:Folder"
  include="[script-1.less,script-2.less]"/>
```

## API / Service Usage

### Core Services

#### `UiLibraryResource`

Sling Model representing a UI library. Provides access to compiled CSS and JavaScript output, included scripts, and dependencies.

#### Compilation Services

The compilation pipeline processes script files through registered compilers (e.g., LESS to CSS, plain CSS passthrough) and optionally minifies the output.

#### Cache Services

Compiled output is cached in the JCR to avoid recompilation on every request. Cache invalidation occurs automatically when source files change.

### Compilers (`compilers/` module)

| Compiler | Description |
|----------|-------------|
| `LessCssCompilerService` | Compiles LESS files to CSS |

### Script Types

| Type | File Model | Content Type |
|------|-----------|--------------|
| LESS CSS | `LessCssFile` | `text/less` |

Additional compilers (SCSS, TypeScript, etc.) can be registered as OSGi services implementing the compiler interface.

### Event Listeners

| Listener | Purpose |
|----------|---------|
| `UiLibraryCachePurgeEventListener` | Purges compiled CSS/JS cache when source files change |

## Dependencies

**Depends on:**

| Module | Purpose |
|--------|---------|
| `kestros-sling-ui-libraries-api` | Service and model interfaces |
| `kestros-sling-ui-libraries-base-compilers` | Base CSS and JavaScript compilers |
| `kestros-osgi-service-utils` | Base service classes and cache infrastructure |
| `kestros-structured-sling-models` | Base Sling Model classes |
| `kestros-validation-api` | Validation framework |

**Depended on by:**

| Module | Purpose |
|--------|---------|
| `kestros-ui-frameworks-core` | Uses UI library compilation for framework output |
| `kestros-component-types-core` | Uses UI library compilation for component view CSS/JS |
| `kestros-cms-foundation` | Depends on UI library services |
