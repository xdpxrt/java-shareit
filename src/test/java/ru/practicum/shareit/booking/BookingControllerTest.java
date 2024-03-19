package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.model.BookingDTO;
import ru.practicum.shareit.booking.model.BookingDTOInput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.ItemDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.USER_ID;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    private final ItemDTO itemDTO = ItemDTO.builder()
            .name("testItem")
            .description("testDescription")
            .available(true)
            .build();
    private final BookingDTOInput bookingDTOInput = BookingDTOInput.builder()
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .itemId(1L).build();
    private final BookingDTO bookingDTO = BookingDTO.builder()
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(itemDTO)
            .build();
    private final Integer from = 0;
    private final Integer size = 10;

    @Test
    void createBookingWhenAllParamsIsValidTest() throws Exception {
        when(bookingService.add(anyLong(), any())).thenReturn(bookingDTO);
        String result = mvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDTOInput)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService).add(1L, bookingDTOInput);
        assertEquals(mapper.writeValueAsString(bookingDTO), result);
    }

    @Test
    void createBookingWhenStartIsNotValidTest() throws Exception {
        BookingDTOInput badBookingDTOInput = BookingDTOInput.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L).build();
        mvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badBookingDTOInput)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, never()).add(anyLong(), any());
    }

    @Test
    void getAllBookingsTest() throws Exception {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";
        when(bookingService.getAllByBooker(anyLong(), any(), any())).thenReturn(List.of(bookingDTO));
        String result = mvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(bookingDTO)), result);
    }

    @Test
    void getAllBookingsBadRequestTest() throws Exception {
        Integer from = -1;
        Integer size = 10;
        mvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getAllByBooker(anyLong(), any(), any());
    }

    @Test
    void getAllByOwnerBookingsTest() throws Exception {
        String state = "ALL";
        when(bookingService.getAllByOwner(anyLong(), any(), any())).thenReturn(List.of(bookingDTO));
        String result = mvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(List.of(bookingDTO)), result);
    }

    @Test
    void getAllByOwnerBookingBadRequestTest() throws Exception {
        Integer from = -1;
        mvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getAllByOwner(anyLong(), any(), any());
    }

    @Test
    void approveBookingTest() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDTO);
        String result = mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", String.valueOf(true))
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(bookingDTO), result);
    }

    @Test
    void approveBookingIsNotValidTest() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDTO);
        mvc.perform(patch("/bookings/{bookingId}", -2)
                        .param("approved", String.valueOf(true))
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, never()).approve(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.get(anyLong(), anyLong())).thenReturn(bookingDTO);
        String result = mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(mapper.writeValueAsString(bookingDTO), result);
    }

    @Test
    void getBookingByIdNotValidTest() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", -1)
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, never()).get(anyLong(), anyLong());
    }
}