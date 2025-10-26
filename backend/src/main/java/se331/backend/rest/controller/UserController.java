package se331.backend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se331.backend.security.user.UserService;

@RestController
@RequestMapping("/api/v1/users") // API Path สำหรับ Admin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<?> promoteUser(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(userService.promoteUserToMember(id));
    }
}