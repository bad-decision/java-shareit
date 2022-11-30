package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@Entity
@Table(name = "Item_Requests", schema = "Public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "itemRequest")
    private Set<Item> items = new HashSet<>();
}
