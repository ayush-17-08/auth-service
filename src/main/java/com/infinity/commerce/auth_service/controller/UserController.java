package com.infinity.commerce.auth_service.controller;

import com.infinity.commerce.auth_service.entity.User;
import com.infinity.commerce.auth_service.pojo.UserDetails;
import com.infinity.commerce.auth_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){

        User createdUser= userService.register(user);
        if(createdUser == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User already registered.");
        }
        else{
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user){
       try{
           String token = userService.loginUser(user);
            if(token==null)  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wrong password");
            else{
                return ResponseEntity.status(HttpStatus.OK).header("authToken",token).body("Successfully loggedin!");
            }
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()+ Arrays.toString(e.getStackTrace()));
       }

    }
}
