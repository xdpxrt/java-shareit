package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.CommentDTO;
import ru.practicum.shareit.item.model.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.USER_ID;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;
    private final ItemDTO itemDTO = ItemDTO.builder()
            .name("testItem")
            .description("testDescription")
            .available(true)
            .build();
    private final Integer from = 0;
    private final Integer size = 10;

    @Test
    void createItemTest() throws Exception {
        when(itemService.addItem(anyLong(), any()))
                .thenReturn(itemDTO);
        String result = mvc.perform(post("/items")
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).addItem(1L, itemDTO);
        assertEquals(mapper.writeValueAsString(itemDTO), result);
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItem(anyLong(), any()))
                .thenReturn(itemDTO);
        String result = mvc.perform(get("/items/{id}", 1)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).getItem(1L, 1L);
        assertEquals(mapper.writeValueAsString(itemDTO), result);
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDTO newItemDTO = ItemDTO.builder()
                .id(1)
                .name("newName")
                .description("newDescription")
                .available(true)
                .build();
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(newItemDTO);
        String result = mvc.perform(patch("/items/{id}", 1)
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newItemDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).updateItem(1L, newItemDTO, 1L);
        assertEquals(mapper.writeValueAsString(newItemDTO), result);
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(itemService.getAllItems(anyLong(), any()))
                .thenReturn(List.of(itemDTO));
        String result = mvc.perform(get("/items", from, size)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).getAllItems(1L, PageRequest.of(from / size, size));
        assertEquals(mapper.writeValueAsString(List.of(itemDTO)), result);
    }

    @Test
    void findItemTest() throws Exception {
        String text = "text";
        when(itemService.findItems(anyString(), any()))
                .thenReturn(List.of(itemDTO));
        String result = mvc.perform(get("/items/search", from, size)
                        .header(USER_ID, 1)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).findItems("text", PageRequest.of(from / size, size));
        assertEquals(mapper.writeValueAsString(List.of(itemDTO)), result);
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDTO commentDTO = CommentDTO.builder()
                .text("text")
                .build();
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDTO);
        String result = mvc.perform(post("/items/{itemId}/comment", 1)
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).addComment(1L, 1L, commentDTO);
        assertEquals(mapper.writeValueAsString(commentDTO), result);
    }
}