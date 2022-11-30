package ru.practicum.shareit.item.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = {"author", "item"})
@ToString(exclude = {"author", "item"})
@Entity
@Table(name = "Comments", schema = "Public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
