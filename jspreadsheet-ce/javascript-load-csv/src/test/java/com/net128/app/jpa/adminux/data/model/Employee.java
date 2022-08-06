package com.net128.app.jpa.adminux.data.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee extends TestIdentifiable {
    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z][A-Za-z -]*[a-z]$")
    @Size(min=2, max=20)
    private String name;

    @Column(nullable = false)
    @Pattern(regexp = "^[A-Z][A-Za-z -]*[a-z]$")
    @Size(min=2, max=20)
    private String department;

    @Column(nullable = false)
    @Min(1)
    @Max(999999)
    private double salary;
}
