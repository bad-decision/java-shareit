package ru.practicum.shareit.request.service;

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
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
class ItemRequestServiceTest {
    private ItemRequestService itemRequestService;
    private ItemRequest itemRequest;
    private ItemRequestAddDto itemRequestAddDto;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    public void prepare() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRequestMapper, itemRequestRepository);
        itemRequest = createDummyItemRequest();
        itemRequestAddDto = createDummyItemRequestAddDto();
    }

    @Test
    public void getRequestWithNotExistUser_shouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(itemRequest.getOwner().getId(), itemRequest.getId()));
    }

    @Test
    public void getNotExistRequest_shouldThrowException() {
        Mockito
                .when(userRepository.findById(itemRequest.getOwner().getId()))
                .thenReturn(Optional.of(itemRequest.getOwner()));
        Mockito
                .when(itemRequestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(itemRequest.getOwner().getId(), itemRequest.getId()));
    }

    @Test
    public void getRequest_shouldReturnItemRequest() {
        Mockito
                .when(userRepository.findById(itemRequest.getOwner().getId()))
                .thenReturn(Optional.of(itemRequest.getOwner()));
        Mockito
                .when(itemRequestRepository.getItemRequest(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));
        ItemRequestDto dto = itemRequestService.getItemRequest(itemRequest.getOwner().getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequest.getId(), dto.getId());
        Assertions.assertEquals(itemRequest.getDescription(), dto.getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), dto.getCreated());
    }

    @Test
    public void getRequestsWithNotExistUser_shouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequests(itemRequest.getOwner().getId()));
    }

    @Test
    public void getRequests_shouldReturnItemRequests() {
        Mockito
                .when(userRepository.findById(itemRequest.getOwner().getId()))
                .thenReturn(Optional.of(itemRequest.getOwner()));
        Mockito
                .when(itemRequestRepository.getItemRequestByOwner(itemRequest.getOwner().getId()))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> dtos = itemRequestService.getItemRequests(itemRequest.getOwner().getId());
        Assertions.assertEquals(1, dtos.size());
        Assertions.assertEquals(itemRequest.getId(), dtos.get(0).getId());
        Assertions.assertEquals(itemRequest.getDescription(), dtos.get(0).getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), dtos.get(0).getCreated());
    }

    @Test
    public void getAllRequestNotExistUser_shouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllItemRequests(itemRequest.getOwner().getId(), 1, 1));
    }

    @Test
    public void getAllRequests_shouldReturnItemRequests() {
        int from = 1;
        int size = 1;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        Mockito
                .when(userRepository.findById(itemRequest.getOwner().getId()))
                .thenReturn(Optional.of(itemRequest.getOwner()));
        Mockito
                .when(itemRequestRepository.getItemRequestsWithPagination(itemRequest.getOwner().getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        List<ItemRequestDto> dtos = itemRequestService.getAllItemRequests(itemRequest.getOwner().getId(), from, size);
        Assertions.assertEquals(1, dtos.size());
        Assertions.assertEquals(itemRequest.getId(), dtos.get(0).getId());
        Assertions.assertEquals(itemRequest.getDescription(), dtos.get(0).getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), dtos.get(0).getCreated());
    }

    @Test
    public void addRequestNotExistUser_shouldThrowException() {
        Mockito
                .when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(itemRequestAddDto));
    }

    @Test
    public void addRequest_shouldReturnAddedRequest() {
        Mockito
                .when(userRepository.findById(itemRequestAddDto.getItemRequestOwnerId()))
                .thenReturn(Optional.of(itemRequest.getOwner()));
        Mockito
                .when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);
        ItemRequestDto dto = itemRequestService.addItemRequest(itemRequestAddDto);
        Assertions.assertEquals(itemRequest.getId(), dto.getId());
        Assertions.assertEquals(itemRequest.getDescription(), dto.getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), dto.getCreated());
    }

    private ItemRequest createDummyItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test request");
        itemRequest.setOwner(createDummyUser());
        itemRequest.setItems(Set.of(createDummyItem()));
        return itemRequest;
    }

    private Item createDummyItem() {
        Item item = new Item();
        item.setName("test");
        item.setDescription("test desc");
        item.setId(1L);
        return item;
    }

    private User createDummyUser() {
        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@mal.ru");
        owner.setName("owner");
        return owner;
    }

    private ItemRequestAddDto createDummyItemRequestAddDto() {
        ItemRequestAddDto itemRequestAddDto = new ItemRequestAddDto();
        itemRequestAddDto.setDescription("test desc");
        itemRequestAddDto.setItemRequestOwnerId(1L);
        return itemRequestAddDto;
    }
}