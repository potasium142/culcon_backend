package com.culcon.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder.Default;

@Data
@Entity
@Builder
@Getter
@Setter
@Table(name = "customer")
public class Customer {
    @Id
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Account account;

    @Column(name = "address")
    @Default
    private String address = "";

    @Column(name = "phone", unique = true, length = 12)
    @Pattern(regexp = "(84|0)[1-9][0-9]{1,9}")
    @Default
    private String phone = "";

    @Column(name = "profile_pic_uri")
    @Default
    private String profilePictureUri = "defaultProfile";
}
