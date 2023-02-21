package antifraud.controller;

import antifraud.dto.AccessDTO;
import antifraud.dto.RoleDTO;
import antifraud.dto.UserDTO;
import antifraud.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(
                userService.saveUser(userDTO),
                HttpStatus.CREATED);
    }

    @PutMapping("/api/auth/access")
    public Object changeAccess(@RequestBody AccessDTO access) {
        userService.changeAccess(access);
        return Map.of("status", "User " + access.getUsername() +
                " " + access.getOperation().toLowerCase() + "ed!");
    }

    @PutMapping("/api/auth/role")
    public UserDTO changeAccess(@RequestBody RoleDTO role) {
        return userService.changeRole(role);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public Object deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return Map.of(
                "username", username,
                "status", "Deleted successfully!");
    }

    @GetMapping("/api/auth/list")
    public List<UserDTO> getUserList(){
        return userService.findAll();
    }
}
