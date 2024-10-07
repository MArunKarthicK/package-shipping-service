package com.abnamro.packageshippingservice.repository;

import com.abnamro.packageshippingservice.model.entity.ShippingOrder;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Long>, JpaSpecificationExecutor<ShippingOrder> {

    Optional<ShippingOrder> findShippingOrderByPackageId(UUID packageId);

    boolean existsShippingOrderByPackageName(String packageName);

    @EntityGraph(attributePaths = {"sender","receiver"})  // to eagerly load
    Page<ShippingOrder> findAll(Specification<ShippingOrder> spec, Pageable pageable);
}
