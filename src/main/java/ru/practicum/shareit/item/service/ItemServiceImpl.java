package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.PermissionException;
import ru.practicum.shareit.item.dto.comment.CommentAddDto;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItems(Long ownerId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Item> items = itemRepository.getOwnerItemsWithBookingsAndComments(ownerId, pageable);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto dto = itemMapper.mapToItemDto(item);
            dto.setLastBooking(bookingMapper.mapToItemBookingDto(findLastBooking(item.getBookings())));
            dto.setNextBooking(bookingMapper.mapToItemBookingDto(findNextBooking(item.getBookings())));
            itemsDto.add(dto);
        }
        return itemsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findByIdWithComments(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        ItemDto dto = itemMapper.mapToItemDto(item);
        dto.setComments(item.getComments().stream().map(commentMapper::mapToCommentDto).collect(Collectors.toSet()));

        if (item.getOwner().getId().equals(userId)) {
            dto.setLastBooking(bookingMapper.mapToItemBookingDto(findLastBooking(item.getBookings())));
            dto.setNextBooking(bookingMapper.mapToItemBookingDto(findNextBooking(item.getBookings())));
        }
        return dto;
    }

    @Override
    public Item addItem(ItemAddDto dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + dto.getOwnerId()));
        ItemRequest itemRequest = null;
        if (dto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Item request not found with id: " + dto.getOwnerId()));
        }

        Item item = itemMapper.mapToItem(dto);
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long ownerId, Item item) {
        Item existItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + item.getId()));

        if (!Objects.equals(ownerId, existItem.getOwner().getId()))
            throw new NotFoundException("User is not an owner of item. OwnerId: " + ownerId + ", item: " + item);

        if (item.getName() != null)
            existItem.setName(item.getName());

        if (item.getDescription() != null)
            existItem.setDescription(item.getDescription());

        if (item.getAvailable() != null)
            existItem.setAvailable(item.getAvailable());

        itemRepository.save(existItem);
        return existItem;
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchItems(String text, boolean isAvailable, int from, int size) {
        if (text == null || text.isEmpty()) return List.of();

        Pageable pageable = PageRequest.of(from / size, size);
        return itemRepository.searchItemsBy(text, isAvailable, pageable).toList();
    }

    @Override
    public Comment addComment(CommentAddDto dto) {
        List<Booking> itemBookings = bookingRepository.getBookingsByBookerAndItem(dto.getAuthorId(), dto.getItemId());
        boolean isBooker = itemBookings.stream().anyMatch(x -> x.getStart().isBefore(LocalDateTime.now()));
        if (!isBooker)
            throw new PermissionException("User cannot add comment because did not book item: " + dto.getItemId());

        Comment comment = commentMapper.mapToComment(dto);
        comment.setItem(itemRepository.getReferenceById(dto.getItemId()));
        comment.setAuthor(userRepository.getReferenceById(dto.getAuthorId()));
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return comment;
    }

    private Booking findLastBooking(Set<Booking> bookings) {
        return bookings.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    private Booking findNextBooking(Set<Booking> bookings) {
        return bookings.stream()
                .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
