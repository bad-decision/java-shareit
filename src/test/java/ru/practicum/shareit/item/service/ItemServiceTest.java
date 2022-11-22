package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.item.ItemAddDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class ItemServiceTest {
    private ItemService itemService;
    private Item item;
    private ItemAddDto itemAddDto;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private CommentMapper commentMapper;

    @BeforeEach
    public void prepare() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, itemRequestRepository, bookingRepository,
                commentRepository, itemMapper, bookingMapper, commentMapper);
        item = createDummyItem();
        itemAddDto = createDummyItemAddDto();
    }

    @Test
    public void addItemWithNotExistOwner_shouldThrowException() {
        Mockito
                .when(userRepository.findById(itemAddDto.getOwnerId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.addItem(itemAddDto));
    }

    @Test
    public void addItemWithNotExistRequest_shouldThrowException() {
        Mockito
                .when(userRepository.findById(itemAddDto.getOwnerId()))
                .thenReturn(Optional.ofNullable(item.getOwner()));
        Mockito
                .when(itemRequestRepository.findById(itemAddDto.getRequestId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.addItem(itemAddDto));
    }

    @Test
    public void addItem_shouldReturnAddedItem() {
        Mockito
                .when(userRepository.findById(itemAddDto.getOwnerId()))
                .thenReturn(Optional.ofNullable(item.getOwner()));
        Mockito
                .when(itemRequestRepository.findById(itemAddDto.getRequestId()))
                .thenReturn(Optional.ofNullable(item.getItemRequest()));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        Item addedItem = itemService.addItem(itemAddDto);
        Assertions.assertEquals(addedItem.getId(), item.getId());
        Assertions.assertEquals(addedItem.getName(), item.getName());
        Assertions.assertEquals(addedItem.getItemRequest().getId(), item.getItemRequest().getId());
        Assertions.assertEquals(addedItem.getOwner().getId(), item.getOwner().getId());
    }

    @Test
    public void getNotExistItem_shouldThrowException() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItem(item.getId(), item.getOwner().getId()));
    }

    @Test
    public void getItem_shouldReturnItem() {
        Mockito
                .when(itemRepository.findByIdWithComments(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        ItemDto dto = itemService.getItem(item.getId(), item.getOwner().getId());
        Assertions.assertEquals(dto.getId(), item.getId());
        Assertions.assertEquals(dto.getName(), item.getName());
        Assertions.assertEquals(dto.getComments().size(), item.getComments().size());
    }

    @Test
    public void getItems_shouldReturnItems() {
        int from = 1;
        int size = 1;
        Pageable pageable = PageRequest.of(from / size, 1);
        Mockito
                .when(itemRepository.getOwnerItemsWithBookingsAndComments(item.getOwner().getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDto> dtos = itemService.getItems(item.getOwner().getId(), from, size);
        Assertions.assertEquals(dtos.size(), 1);
        Assertions.assertEquals(dtos.get(0).getId(), item.getId());
    }

    @Test
    public void deleteNotExistItem_shouldThrowException() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.deleteItem(item.getId()));
    }

    @Test
    public void updateNotExistItem_shouldThrowException() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(item.getOwner().getId(), item));
    }

    @Test
    public void updateItemNotOwner_shouldThrowException() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(userRepository.findById(item.getOwner().getId() + 1))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(item.getOwner().getId() + 1, item));
    }

    @Test
    public void updateItem_shouldReturnUpdatedItem() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(userRepository.findById(item.getOwner().getId()))
                .thenReturn(Optional.ofNullable(item.getOwner()));

        item.setName("updated_name");
        item.setAvailable(false);
        Item updatedItem = itemService.updateItem(item.getOwner().getId(), item);
        Assertions.assertEquals(updatedItem.getId(), item.getId());
        Assertions.assertEquals(updatedItem.getName(), item.getName());
        Assertions.assertEquals(updatedItem.getAvailable(), item.getAvailable());
    }

    @Test
    public void searchItems_shouldReturnItems() {
        int from = 1;
        int size = 1;
        Pageable pageable = PageRequest.of(from / size, 1);

        Mockito
                .when(itemRepository.searchItemsBy(item.getName(), item.getAvailable(), pageable))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<Item> items = itemService.searchItems(item.getName(), item.getAvailable(), from, size);
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item.getName(), items.get(0).getName());
        Assertions.assertEquals(item.getAvailable(), items.get(0).getAvailable());
    }

    private Item createDummyItem() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(createDummyUser());
        item.setName("test item");
        item.setAvailable(true);
        item.setDescription("test description");
        item.setItemRequest(createDummyRequest());
        return item;
    }

    private ItemRequest createDummyRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test request");
        return itemRequest;
    }

    private User createDummyUser() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@mal.ru");
        owner.setName("owner");
        return owner;
    }

    private ItemAddDto createDummyItemAddDto() {
        ItemAddDto itemAddDto = new ItemAddDto();
        itemAddDto.setOwnerId(1L);
        itemAddDto.setRequestId(1L);
        itemAddDto.setName(item.getName());
        itemAddDto.setName(item.getDescription());
        return itemAddDto;
    }
}