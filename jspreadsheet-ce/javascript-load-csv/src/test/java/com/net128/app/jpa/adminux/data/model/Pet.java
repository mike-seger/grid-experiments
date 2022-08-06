package com.net128.app.jpa.adminux.data.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pet extends TestIdentifiable {
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z][A-Za-z -]*[a-z]$")
    @Size(min=2, max=30)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Species species;

    @Column(nullable = false)
    @DecimalMin("0.1")
    @DecimalMax("9999")
    private double weight;
}
