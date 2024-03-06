//package ru.practicum.shareit.item;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import ru.practicum.shareit.error.exception.AccessException;
//import ru.practicum.shareit.error.exception.NotFoundException;
//import ru.practicum.shareit.error.exception.ValidationException;
//import ru.practicum.shareit.user.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//public class ItemServiceTest {
//    private ItemService itemService;
//    private UserService userService;
//    private final ItemRepository itemRepository;
//    private final UserRepository userRepository;
//    private final ItemMapper itemMapper;
//    private final UserMapper userMapper;
//    private ItemDTO itemDTO;
//    private UserDTO userDTO;
//
//    @BeforeEach
//    public void init() {
//        itemService = new ItemServiceImpl(itemRepository, userRepository, itemMapper);
//        userService = new UserServiceImpl(userRepository, userMapper);
//        itemDTO = new ItemDTO(1, "Item", "Description", true);
//        userDTO = new UserDTO(1, "Name", "email@email.com");
//    }
//
//    @Test
//    public void itemCreateTest() {
//        userService.addUser(userDTO);
//        assertEquals(itemDTO, itemService.addItem(1L, itemDTO));
//    }
//
//    @Test
//    public void itemCreateWithoutUserTest() {
//        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemDTO));
//    }
//
//    @Test
//    public void itemCreateWithoutAvailableTest() {
//        userService.addUser(userDTO);
//        assertThrows(ValidationException.class, () -> itemService.addItem(1L,
//                new ItemDTO(1, "Item", "Description", null)));
//    }
//
//    @Test
//    public void itemCreateWithoutNameTest() {
//        userService.addUser(userDTO);
//        assertThrows(ValidationException.class, () -> itemService.addItem(1L,
//                new ItemDTO(1, null, "Description", true)));
//    }
//
//    @Test
//    public void itemCreateWithoutDescriptionTest() {
//        userService.addUser(userDTO);
//        assertThrows(ValidationException.class, () -> itemService.addItem(1L,
//                new ItemDTO(1, "Item", null, true)));
//    }
//
//    @Test
//    public void itemUpdateTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        ItemDTO updateItemDTO = new ItemDTO(1, "newName", "newDescription", false);
//        assertEquals(updateItemDTO, itemService.updateItem(1L, updateItemDTO, 1));
//    }
//
//    @Test
//    public void itemUpdateByOtherUserTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        assertThrows(AccessException.class, () -> itemService.updateItem(2L, itemDTO, 1));
//    }
//
//    @Test
//    public void itemUpdateNameTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        itemDTO.setName("newName");
//        ItemDTO updateItemDTO = new ItemDTO(1, "newName", null, null);
//        assertEquals(itemDTO, itemService.updateItem(1L, updateItemDTO, 1));
//    }
//
//    @Test
//    public void itemUpdateDescriptionTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        itemDTO.setDescription("newDescription");
//        ItemDTO updateItemDTO = new ItemDTO(1, null, "newDescription", null);
//        assertEquals(itemDTO, itemService.updateItem(1L, updateItemDTO, 1));
//    }
//
//    @Test
//    public void itemUpdateAvailableTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        itemDTO.setAvailable(false);
//        ItemDTO updateItemDTO = new ItemDTO(1, null, null, false);
//        assertEquals(itemDTO, itemService.updateItem(1L, updateItemDTO, 1));
//    }
//
//    @Test
//    public void itemGetTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        assertEquals(itemDTO, itemService.getItem(1));
//    }
//
//    @Test
//    public void itemGetAllTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        assertEquals(1, itemService.getAllItems(1).size());
//        assertEquals(itemDTO, itemService.getAllItems(1).get(0));
//    }
//
//    @Test
//    public void itemSearchTest() {
//        userService.addUser(userDTO);
//        itemService.addItem(1L, itemDTO);
//        assertEquals(1, itemService.findItems("DESC").size());
//        assertEquals(itemDTO, itemService.findItems("DESC").get(0));
//    }
//}