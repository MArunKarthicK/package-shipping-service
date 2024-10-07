package com.abnamro.packageshippingservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private long id ;

    @NotBlank(message = "firstName is required")
    @Column(name="first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Column(name="last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "street is required")
    @Column(name="street", nullable = false)
    private String street;

    @NotBlank(message = "postalcode is required")
    @Column(name="postal_code", nullable = false)
    private String postalCoda;

    @NotBlank(message = "city is required")
    @Column(name= "city", nullable = false)
    private String city;

}
