package com.abnamro.packageshippingservice.model.entity;

import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@Getter
@Setter
@Entity
@Table(name = "shipping_orders")
@AllArgsConstructor
@NoArgsConstructor
public class ShippingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "shipping_order_id", updatable = false, nullable = false)
    private long shippingOrderId; // primary key

    @Column(name = "package_id", unique = true, updatable = false, nullable = false)
    private UUID packageId; // unique key to expose outside

    @NotBlank(message = "Package packageName is required")
    @Column(name = "package_name", unique = true, nullable = false, length = 100)
    private String packageName;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Column(name = "weight_in_grams", nullable = false)
    private Double weightInGrams;

    @NotNull(message = "Receiver employeeId is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_emp_id", referencedColumnName = "id")
    private Employee receiver;

    @NotNull(message = "Sender employeeId is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_emp_id", referencedColumnName = "id")
    private Employee sender;

    @CreationTimestamp
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @NotNull(message = "Package order status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private OrderStatusEnum status;

    @Column(name = "delivered_date",nullable = false)
    private LocalDateTime deliveredDate; // Nullable; set when status is DELIVERED

}
