package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long itemRequestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        ItemRequest itemRequest = itemRequestRepository.getItemRequest(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + itemRequestId));
        return itemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Long requestOwnerId) {
        userRepository.findById(requestOwnerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + requestOwnerId));

        List<ItemRequest> itemRequests = itemRequestRepository.getItemRequestByOwner(requestOwnerId);
        return itemRequests.stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        if (from < 0 || size <= 0)
            throw new IllegalArgumentException("Argument size or from is incorrect");

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        Page<ItemRequest> itemRequests = itemRequestRepository.getItemRequestsWithPagination(userId, pageable);
        return itemRequests.stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto addItemRequest(ItemRequestAddDto dto) {
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(dto);
        User itemRequestOwner = userRepository.findById(dto.getItemRequestOwnerId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + dto.getItemRequestOwnerId()));
        itemRequest.setOwner(itemRequestOwner);
        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.mapToItemRequestDto(itemRequest);
    }
}
