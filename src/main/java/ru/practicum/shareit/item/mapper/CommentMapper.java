package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.comment.CommentAddDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper
public interface CommentMapper {
    @Mapping(target="authorName", source="author.name")
    CommentDto mapToCommentDto(Comment comment);

    Comment mapToComment(CommentAddDto commentDto);
}