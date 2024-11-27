package com.culcon.backend.models.record;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;


@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(
            name = "product_types",
            columnDefinition = "int[]"
    )
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Enumerated(EnumType.ORDINAL)
    private List<ProductType> productTypes;
}
