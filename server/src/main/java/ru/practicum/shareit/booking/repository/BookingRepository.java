package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
            Long bookerId, LocalDateTime now, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2, PageRequest pageRequest);

    Page<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(
            Long bookerId, BookingStatus bookingStatus, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
            Long ownerId, LocalDateTime now, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now1, LocalDateTime now2, PageRequest pageRequest);

    Page<Booking> findAllByItemOwnerIdAndStatusIsOrderByStartDesc(
            Long ownerId, BookingStatus bookingStatus, PageRequest pageRequest);

    List<Booking> findByItemIdInAndStatusNot(List<Long> itemIds, BookingStatus bookingStatus);

    Optional<Booking> findFirstByItemIdAndBookerIdOrderByStart(Long itemId, Long bookerId);
}