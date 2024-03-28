package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CommentDTO commentDTO);

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    CommentDTO toCommentDTO(Comment comment);

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    List<CommentDTO> toCommentDTO(List<Comment> comments);
}