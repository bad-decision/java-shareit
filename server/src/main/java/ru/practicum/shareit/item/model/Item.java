package ru.practicum.shareit.item.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = {"owner", "comments", "bookings", "itemRequest"})
@ToString(exclude = {"owner", "comments", "bookings", "itemRequest"})
@Entity
@Table(name = "Items", schema = "Public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "item_request_id")
    private ItemRequest itemRequest;

    @OneToMany(mappedBy = "item")
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<Comment> comments = new HashSet<>();
}
