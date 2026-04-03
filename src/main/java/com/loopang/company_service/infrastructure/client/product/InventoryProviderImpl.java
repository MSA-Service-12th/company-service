package com.loopang.company_service.infrastructure.client.product;

import com.loopang.company_service.domain.service.InventoryProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryProviderImpl implements InventoryProvider {

  private final ProductFeignClient productClient;

  @Override
  public boolean hasRemainingStock(UUID companyId) {
    // GET /api/v1/products/exists?companyId={companyId} 호출 가정
    return productClient.existsByCompanyId(companyId);
  }
}
