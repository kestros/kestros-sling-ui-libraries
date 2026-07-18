# kestros-sling-ui-libraries

UiLibraries are managed compilations of CSS and JavaScript files that can be included on Sling resources. This repo provides the core implementation (`kestros-sling-ui-libraries-core`) for the UiLibrary model, retrieval services, compilation pipeline, cache management, and Sling resource-type-bound servlets that serve compiled CSS and JS.

---

## Purpose

`kestros-sling-ui-libraries` sits in the **Kestros frontend rendering layer**. It defines the `kes:UiLibrary` resource type and the services that compile and serve aggregated CSS/JavaScript output from JCR-stored script files.

Architecture position:
- Depends on: `kestros-structured-sling-models`, `kestros-osgi-service-utils`, `kestros-validation-api`
- Consumed by: `kestros-ui-frameworks-core`, `kestros-sitebuilding-core`, `kestros-component-types-core`

The repo contains two Maven modules:
- `core` — OSGi bundle with models, services, and servlets
- `compilers` — LESS compiler support
- `minifiers` — optional JS/CSS minification support

---

## Installation and Build

**Maven coordinates (core bundle):**

```
groupId:    io.kestros.commons
artifactId: kestros-sling-ui-libraries-core
version:    0.2.8
```

**Build:**

```bash
cd kestros-sling-ui-libraries
mvn clean package -q
```

**Deploy to Kestros CMS (port 8000):**

```bash
curl -u admin:$KESTROS_PASSWORD \
  -F "action=install" \
  -F "bundlestart=true" \
  -F "bundlefile=@core/target/kestros-sling-ui-libraries-core-0.2.8.jar" \
  "http://192.168.86.216:8000/system/console/bundles"
```

**Verify bundle is Active:**

```bash
curl -s -u admin:$KESTROS_PASSWORD \
  "http://192.168.86.216:8000/system/console/bundles.json" | \
  python3 -c "
import sys, json
d = json.load(sys.stdin)
for b in d['data']:
    if 'kestros-sling-ui-libraries' in b.get('symbolicName',''):
        print(b['state'], b['symbolicName'])
"
```

---

## Configuration

### Service User

The `UiLibraryRetrievalServiceImpl` uses the `ui-library-manager` service user. This service user must be provisioned in the Sling instance with read access to `/apps`, `/libs`, `/etc`, and `/var/cache/ui-libraries`.

### OSGi Configuration

No explicit OSGi config properties are required for default operation. Optional tuning via `UiLibraryConfigurationService` configuration if a custom implementation is registered.

**Cache storage path:** `/var/cache/ui-libraries` (JCR-backed, auto-created)

---

## API and Service Usage

### Resource Type

UiLibrary resources use the `kes:UiLibrary` JCR primary type. Structure a UI library in the repository as follows:

```xml
<!-- Root resource: /apps/myproject/ui-libraries/my-library -->
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0"
  xmlns:jcr="http://www.jcp.org/jcr/1.0"
  jcr:primaryType="kes:UiLibrary"
  jcr:title="My UI Library"/>
```

```xml
<!-- CSS folder: /apps/myproject/ui-libraries/my-library/css -->
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0"
  xmlns:jcr="http://www.jcp.org/jcr/1.0"
  jcr:primaryType="sling:Folder"
  include="[styles.less,overrides.css]"/>
```

```xml
<!-- JS folder: /apps/myproject/ui-libraries/my-library/js -->
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0"
  xmlns:jcr="http://www.jcp.org/jcr/1.0"
  jcr:primaryType="sling:Folder"
  include="[main.js,utils.js]"/>
```

### Servlet Endpoints

Both servlets are registered by resource type `kes:UiLibrary` and extension.

| Endpoint | Method | Resource Type | Extension | Description |
|---|---|---|---|---|
| `{library-path}.css` | GET | `kes:UiLibrary` | `.css` | Returns compiled and optionally minified CSS output |
| `{library-path}.js` | GET | `kes:UiLibrary` | `.js` | Returns compiled and optionally minified JavaScript output |

**Example:**

```bash
# Get compiled CSS for a UI library at /apps/myproject/ui-libraries/my-library
curl -u admin:$KESTROS_PASSWORD \
  "http://192.168.86.216:8000/apps/myproject/ui-libraries/my-library.css"
```

Response: compiled CSS text (content-type: `text/css`)

```bash
# Get compiled JS
curl -u admin:$KESTROS_PASSWORD \
  "http://192.168.86.216:8000/apps/myproject/ui-libraries/my-library.js"
```

Response: compiled JavaScript text (content-type: `application/javascript`)

### UiLibraryRetrievalService

```java
// Inject via OSGi @Reference
@Reference
private UiLibraryRetrievalService uiLibraryRetrievalService;

// Retrieve a UiLibrary model (caller provides resolver — resolver stays open during use)
UiLibrary library = uiLibraryRetrievalService.getUiLibrary(
    "/apps/myproject/ui-libraries/my-library", resourceResolver);

// Or retrieve with an internally-managed service resolver
UiLibrary library = uiLibraryRetrievalService.getUiLibrary(
    "/apps/myproject/ui-libraries/my-library");
```

| Method | Returns | Description |
|---|---|---|
| `getUiLibrary(String path, ResourceResolver resolver)` | `UiLibrary` | Adapt resource at path to `UiLibraryResource` using provided resolver |
| `getUiLibrary(String path)` | `UiLibrary` | Same but opens a service resolver internally |

### UiLibraryResource (Sling Model)

Adaptable from `Resource` where `jcr:primaryType=kes:UiLibrary`.

| Method | Returns | Description |
|---|---|---|
| `getCssPath()` | `String` | Path to compiled CSS — `{libraryPath}.css` |
| `getJsPath()` | `String` | Path to compiled JS — `{libraryPath}.js` |
| `getIncludedFileNames(ScriptType)` | `List<String>` | File names in the `include` array for the given script type folder |
| `getScriptFiles(List<ScriptType>, String folder)` | `List<T extends ScriptFile>` | Resolved script file models from the specified folder |

### HTL Template Integration

Include a UI library's compiled output in HTL templates:

```html
<!-- Include CSS -->
<sly data-sly-use.lib="/libs/kestros/commons/ui-libraries.html">
    <sly data-sly-call="${lib.includeCss @ uiLibrary=myUiLibrary}"/>
</sly>

<!-- Include JavaScript -->
<sly data-sly-use.lib="/libs/kestros/commons/ui-libraries.html">
    <sly data-sly-call="${lib.includeJs @ uiLibrary=myUiLibrary}"/>
</sly>
```

### Supported Script Types

| Type | Extension | Notes |
|---|---|---|
| JavaScript | `.js` | Concatenated in `include` order |
| CSS | `.css` | Concatenated in `include` order |
| LESS | `.less` | Compiled to CSS via LESS compiler module |

### Cache Management

Compiled output is cached under `/var/cache/ui-libraries`. The cache is invalidated automatically when any resource under `/libs`, `/apps`, or `/etc` is modified (via `UiLibraryCachePurgeEventListener`).

To manually purge the cache, delete all nodes under `/var/cache/ui-libraries` in the JCR.

---

## Dependencies

### Upstream (this module depends on)

| Artifact | Version |
|---|---|
| `io.kestros.commons:kestros-structured-sling-models` | `[0.2.5,0.2.99]` |
| `io.kestros.commons:kestros-osgi-service-utils` | `[0.1.10,0.1.99]` |
| `io.kestros.commons:kestros-validation-api` | `[0.2.1,0.2.99]` |
| `io.kestros.commons:kestros-sling-ui-libraries-api` | `[0.1.0,0.1.99]` |
| `io.kestros.commons:kestros-sling-ui-libraries-base-compilers` | `[0.0.4,0.0.99]` |

### Downstream (depends on this module)

- `kestros-ui-frameworks-core` — extends UiLibrary model for UI framework themes
- `kestros-component-types-core` — uses UI libraries for component view output
- `kestros-sitebuilding-core` — uses UI libraries for site-level CSS/JS aggregation
