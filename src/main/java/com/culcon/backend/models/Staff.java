package com.culcon.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Setter
@Table(name = "staff")
public class Staff {
  @Id private String id;

  @OneToOne
  @MapsId
  @JoinColumn(name = "id")
  private Account account;

  private String address;
  private String phone;
  private String ssn;
}
