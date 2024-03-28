package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.model.ItemRequestDTO;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)

public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private final ItemRequestDTO itemRequestDTO = ItemRequestDTO
            .builder()
            .id(1L)
            .description("testDescription")
            .build();

    @Test
    void addRequestTest() throws Exception {
        when(itemRequestService.addItemRequest(anyLong(), any())).thenReturn(itemRequestDTO);
        String result = mvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).addItemRequest(1L, itemRequestDTO);
        assertEquals(mapper.writeValueAsString(itemRequestDTO), result);
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), any()))
                .thenReturn(itemRequestDTO);
        String result = mvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).getItemRequest(1L, 1L);
        assertEquals(mapper.writeValueAsString(itemRequestDTO), result);
    }

    @Test
    void getItemRequestsTest() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(), any()))
                .thenReturn(List.of(itemRequestDTO));
        Integer from = 0;
        Integer size = 10;
        String result = mvc.perform(get("/requests/all", from, size)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).getItemRequests(1L, PageRequest.of(from / size, size,
                Sort.by("created").descending()));
        assertEquals(mapper.writeValueAsString(List.of(itemRequestDTO)), result);
    }

    @Test
    void getOwnerItemRequestsTest() throws Exception {
        when(itemRequestService.getOwnerItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestDTO));
        String result = mvc.perform(get("/requests")
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).getOwnerItemRequests(1L);
        assertEquals(mapper.writeValueAsString(List.of(itemRequestDTO)), result);
    }
}