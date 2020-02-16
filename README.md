# Kestros UI Libraries
UiLibraries are managed compilations of CSS and JavaScript files that can be included on Sling resources.

## Creating UI Libraries

Root level resource
```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0"
  xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:kes="http://kestros.slingware.com/kes/1.0"
  jcr:primaryType="kes:UiLibrary"
  jcr:title="My UI Library"/>
```

CSS Script Folder
```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
  jcr:primaryType="sling:Folder"
  include="[script-1.less,script-2.less]"/>
```

JavaScript Script Folder
```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
  jcr:primaryType="sling:Folder"
  include="[script-1.js,script-2.js]"/>
```


## Include UiLibrary Scripts

### JavaScript
```
<sly data-sly-use.lib="/libs/kestros/commons/ui-libraries.html">
    <sly data-sly-call="${lib.includeJs @ uiLibrary=myUiLibrary}"/>
</sly>
```

### CSS
```
  <sly data-sly-use.lib="/libs/kestros/commons/ui-libraries.html">
    <sly data-sly-call="${lib.includeCss @ uiLibrary=myUiLibrary}"/>
  </sly>
```

  
## Supported Script Types
Scripts types that are currently supported are:
* JavaScript
* CSS
* Less

## Clearing UI Library Cache
Cached UiLibrary scripts can be cleared in one of two ways.

* Manually delete the cached resources from `/var/cache/ui-libraries`
* Make a change to any resource in `/libs`, `/apps`, or `/etc`.
