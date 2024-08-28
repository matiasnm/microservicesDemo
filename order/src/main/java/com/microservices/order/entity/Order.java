package com.microservices.order.entity;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "`order`")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

   @Id
   @UuidGenerator
   private String id;

   @NotEmpty
   private String product_id;

   @NotEmpty
   private String sku;

   @NotNull
   @Min(1)
   private Integer quantity;

   @Column(name = "created_at", updatable = false)
   @NotNull
   private Instant createdAt;
   
   @Column(name = "updated_at")
   @NotNull
   private Instant updatedAt;
   
   @PreUpdate
   @PrePersist
   public void updateTimeStamps() {
      updatedAt = Instant.now();
      if (createdAt == null) {
         createdAt = Instant.now();
      }
   }

}