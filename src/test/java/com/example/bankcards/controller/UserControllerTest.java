package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    void getAll_ShouldReturnUsers() {
        List<UserDTO> expectedUsers = List.of(
                new UserDTO(1L,
                        "user1",
                        "First",
                        "Second",
                        "Surname",
                        LocalDate.of(1990, 1, 1),
                        "USER"),
                new UserDTO(2L,
                        "user2",
                        "First",
                        "Second",
                        "Surname",
                        LocalDate.of(1990, 1, 1),
                        "USER")
        );

        when(userService.getAllUsers()).thenReturn(expectedUsers);

        List<UserDTO> actualUsers = userController.getAll();

        assertThat(actualUsers).isEqualTo(expectedUsers);

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void delete_ShouldCallServiceDeleteUser() {
        Long userId = 42L;

        doNothing().when(userService).deleteUser(userId);

        userController.delete(userId);

        verify(userService, times(1)).deleteUser(userId);
    }
}
