package com.microservices.product.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    @NotBlank
    private String sku;

    @NotNull
    private Float price;

    @NotNull
    @CreatedDate
    private OffsetDateTime createdAt;

    @NotNull
    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @Version
    private Integer version;

}