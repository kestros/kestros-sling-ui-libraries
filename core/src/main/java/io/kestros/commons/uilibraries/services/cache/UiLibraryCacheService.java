package io.kestros.commons.uilibraries.services.cache;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.util.Map;

/**
 * Service for handling UiLibrary caches.
 */
public interface UiLibraryCacheService extends CacheService {

  /**
   * Retrieves the cached output for a specified UiLibrary.
   *
   * @param uiLibrary UiLibrary to retrieve cached script for.
   * @param scriptType ScriptType (CSS or JS) to retrieve.
   * @param minified Whether to retrieved the minified version.
   * @return The cached output for a specified UiLibrary.
   * @throws CacheRetrievalException Cached output could not be retrieved.
   */
  String getCachedOutput(UiLibrary uiLibrary, ScriptType scriptType, boolean minified)
      throws CacheRetrievalException;

  /**
   * Caches all scripts for the specified UiLibrary.
   *
   * @param uiLibrary UiLibrary to cache.
   * @param cacheMinified Whether to cache minified scripts.
   * @throws CacheBuilderException Cache failed to build for specified UiLibrary.
   */
  void cacheUiLibraryScripts(UiLibrary uiLibrary, boolean cacheMinified)
      throws CacheBuilderException;

  /**
   * Caches all scripts for the specified UiLibrary.
   *
   * @param uiLibraryPath UiLibrary to cache.
   * @param cacheMinified Whether to cache minified scripts.
   * @throws CacheBuilderException Cache failed to build for specified UiLibrary.
   */
  void cacheUiLibraryScripts(String uiLibraryPath, boolean cacheMinified)
      throws CacheBuilderException;

  void addCacheCreationJob(Map<String, Object> jobProperties);

  Map<String, Object> getCacheCreationJobProperties(UiLibrary uiLibrary, Boolean cacheMinified);
}
