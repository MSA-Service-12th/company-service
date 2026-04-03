package com.loopang.company_service.infrastructure.persistence;

import com.loopang.company_service.domain.dto.CompanySearchCondition;
import com.loopang.company_service.domain.entity.Company;
import com.loopang.company_service.domain.entity.QCompany;
import com.loopang.company_service.domain.repository.CompanyQueryRepository;
import com.loopang.company_service.domain.vo.CompanyStatus;
import com.loopang.company_service.domain.vo.CompanyType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompanyQueryRepositoryImpl implements CompanyQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QCompany company = QCompany.company;

  @Override
  public Page<Company> searchCompanies(CompanySearchCondition condition, Pageable pageable) {
    // 1. 데이터 조회 쿼리
    List<Company> content = queryFactory.selectFrom(company).where(
            // 명세서 조건 반영
            combineKeyword(condition.keyword()),          // keyword (이름 OR 주소)
            nameContains(condition.managerName()),        // 단독 name
            typeEq(condition.type()),                     // type (equals)
            statusEq(condition.status()),                 // status (equals)
            managerNameContains(condition.managerName()), // managerName (contains)
            hubIdEq(condition.hubId()),                   // hubId (equals)
            company.deletedAt.isNull()                    // Soft Delete 기본 필터
        ).offset(pageable.getOffset()).limit(pageable.getPageSize())
        .orderBy(getOrderSpecifier(pageable))             // 동적 정렬 지원
        .fetch();

    // 2. 카운트 쿼리
    Long total = queryFactory.select(company.count()).from(company)
        .where(combineKeyword(condition.keyword()), nameContains(condition.managerName()),
            typeEq(condition.type()), statusEq(condition.status()),
            managerNameContains(condition.managerName()), hubIdEq(condition.hubId()),
            company.deletedAt.isNull())
        .fetchOne();

    return new PageImpl<>(content, pageable, total != null ? total : 0L);
  }

  // --- 조건절(BooleanExpression) 상세 구현 ---

  // keyword: 업체명 또는 주소(fullAddress)에 키워드가 포함된 경우 (OR 연산)
  private BooleanExpression combineKeyword(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return null;
    }
    return company.name.contains(keyword).or(company.address.fullAddress.contains(keyword));
  }

  private BooleanExpression nameContains(String name) {
    return (name != null && !name.isBlank()) ? company.name.contains(name) : null;
  }

  private BooleanExpression typeEq(CompanyType type) {
    return type != null ? company.type.eq(type) : null;
  }

  private BooleanExpression statusEq(CompanyStatus status) {
    return status != null ? company.status.eq(status) : null;
  }

  private BooleanExpression managerNameContains(String managerName) {
    return (managerName != null && !managerName.isBlank()) ? company.manager.managerName.contains(
        managerName) : null;
  }

  private BooleanExpression hubIdEq(UUID hubId) {
    return hubId != null ? company.hub.hubId.eq(hubId) : null;
  }

  // Pageable의 Sort 정보를 Querydsl의 OrderSpecifier로 변환 (createdAt, desc 등 대응)
  private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
    if (!pageable.getSort().isEmpty()) {
      for (Sort.Order order : pageable.getSort()) {
        // 명세서의 기본값인 createdAt 대응
        if (order.getProperty().equals("createdAt")) {
          return order.isAscending() ? company.createdAt.asc() : company.createdAt.desc();
        }
        // type 등 다른 정렬 조건 추가 시 확장 가능
        if (order.getProperty().equals("type")) {
          return order.isAscending() ? company.type.asc() : company.type.desc();
        }
      }
    }
    return company.createdAt.desc(); // 기본값
  }
}
