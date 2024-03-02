//package ru.practicum.shareit.request.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "requests")
//@AllArgsConstructor
//@NoArgsConstructor
//public class ItemRequest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//    @Column(name = "description", nullable = false)
//    private String description;
//    @Column(name = "requestor_id", nullable = false)
//    private long requestor;
//    private LocalDateTime created;
//}
