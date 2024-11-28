package com.culcon.backend.models.user;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "post_interaction")
public class PostInteraction {
    @EmbeddedId
    private PostInteractionId id;

    private Integer rated;
    private Boolean bookmarked;
    private String comment;
}
