package com.culcon.backend.models.user;


import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PostInteractionId {
    private String postId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
