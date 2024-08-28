package com.microservices.inventory.entity;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "`inventory`")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    
   @Id
   @UuidGenerator
   private String id;
   
   @NotEmpty()
   private String sku;

   @NotNull()
   private Integer stock;
  
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