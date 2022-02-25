package com.hoaxify.ws.user;


import com.hoaxify.ws.error.ApiError;
import com.hoaxify.ws.shared.CurrentUser;
import com.hoaxify.ws.shared.GenericResponse;
import com.hoaxify.ws.user.vm.UserUpdateVM;
import com.hoaxify.ws.user.vm.UserVM;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/1.0")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;


    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse createUser(@Valid @RequestBody User user){

        userService.save(user);
        return new GenericResponse("user created");

    }

    @GetMapping("/users")
    Page<UserVM> getUsers(Pageable page, @CurrentUser User user){
        return userService.getUsers(page,user).map(UserVM::new);
    }

    @GetMapping("/users/{username}")
    UserVM getUser(@PathVariable String username){
        User user = userService.getByUsername(username);
        return new UserVM(user);
    }

    @PutMapping("/users/{username}")
    @PreAuthorize("#username == principal.username")
    UserVM updateUser(@Valid @RequestBody UserUpdateVM updateUser, @PathVariable String username){

        User user= userService.updateUser(username,updateUser);
        return   new UserVM(user);
    }
    @DeleteMapping("/users/{username}")
    @PreAuthorize("#username == principal.username")
    GenericResponse deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return  new GenericResponse(" User is removed");
    }
}






