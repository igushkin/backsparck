package com.example.backspark.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocksTransaction {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private Integer cottonPart;
    @Column(nullable = false)
    private Integer quantity;
}