package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment_likes")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean islike;

    private Long commentId;

    private Long authorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentLike)) return false;
        return id != null && id.equals(((CommentLike) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
