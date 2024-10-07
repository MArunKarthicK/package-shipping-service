package com.abnamro.packageshippingservice.repository;


import com.abnamro.packageshippingservice.model.entity.Employee;
import com.abnamro.packageshippingservice.model.entity.ShippingOrder;
import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ShippingOrderSpecifications {
    /**
     * Creates a JPA Specification to filter shipping orders by the specified employeeId
     * and package order status.
     *
     * <p>This specification performs an inner join on the "sender" employee and a inner join
     * on the "receiver" employee. It constructs predicates based on the provided employee ID
     * and package order status to filter the shipping orders accordingly.</p>
     *
     * @param employeeId    the ID of the employee whose shipping orders are to be retrieved
     *                      (must not be null)
     * @param packageStatus the status of the shipping order to filter by; can be null,
     *                      in which case the status filter is ignored
     * @return a Specification that can be used to filter ShippingOrder entities based
     * on the provided employee ID and package order status
     */
    public static Specification<ShippingOrder> joinEmployeeWithIdAndFilterByPackageStatus(Long employeeId, OrderStatusEnum packageStatus) {
        return (root, query, criteriaBuilder) -> {
            Join<ShippingOrder, Employee> senderJoin = root.join("sender", JoinType.INNER);
            Join<ShippingOrder, Employee> receiverJoin = root.join("receiver", JoinType.INNER);

            Predicate employeePredicate = criteriaBuilder.equal(senderJoin.get("id"), employeeId);
            Predicate statusPredicate = (packageStatus != null)
                    ? criteriaBuilder.equal(root.get("status"), packageStatus)
                    : criteriaBuilder.conjunction();

            return criteriaBuilder.and(employeePredicate, statusPredicate);
        };
    }
}
