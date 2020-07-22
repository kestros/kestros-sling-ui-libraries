package io.kestros.commons.uilibraries.healthchecks;

import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import org.apache.felix.hc.annotation.Async;
import org.apache.felix.hc.annotation.HealthCheckMBean;
import org.apache.felix.hc.annotation.HealthCheckService;
import org.apache.felix.hc.annotation.ResultTTL;
import org.apache.felix.hc.annotation.Sticky;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.felix.hc.api.HealthCheck;
import org.apache.felix.hc.api.Result;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component
@HealthCheckService(name = "UiLibraryCacheService Check",
                    tags = {"kestros", "ui-libraries"})
@Async(intervalInSec = 60)
@ResultTTL(resultCacheTtlInMs = 10000)
@HealthCheckMBean(name = "UiLibraryCacheServiceHealthCheck")
@Sticky(keepNonOkResultsStickyForSec = 10)
public class UiLibraryCacheServiceHealthCheck implements HealthCheck {


  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCacheService uiLibraryCacheService;


  @Override
  public Result execute() {
    FormattingResultLog log = new FormattingResultLog();
    if (uiLibraryCacheService == null) {
      log.critical("No UiLibraryCacheService is registered.");
    } else if (!uiLibraryCacheService.isLive()) {
      log.warn("UiLibraryCacheService is registered, but is not live.");
    } else {
      log.info("UiLibraryCacheService is registered and running properly.");
    }
    return new Result(log);
  }
}
