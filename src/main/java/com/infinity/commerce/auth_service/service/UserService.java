package com.infinity.commerce.auth_service.service;

import com.infinity.commerce.auth_service.entity.User;
import com.infinity.commerce.auth_service.entity.UserRole;
import com.infinity.commerce.auth_service.enums.Role;
import com.infinity.commerce.auth_service.pojo.UserDetails;
import com.infinity.commerce.auth_service.redis.RedisCacheManager;
import com.infinity.commerce.auth_service.repository.UserRepository;
import com.infinity.commerce.auth_service.repository.UserRoleRepository;
import com.infinity.commerce.auth_service.utlis.JwtUtil;
import com.infinity.commerce.auth_service.utlis.PasswordUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    RedisCacheManager redisCacheManager;

    @Transactional
    public User register(User user) {
        if(userRepository.findByUsername(user.getUsername()) != null){
            //already registered user!
            return null;
        }
        else{
            user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
            //encoding the simple password
            //then saving in the db---> username , password
            userRepository.save(user);
            UserRole userRole = new UserRole(user.getId(), Role.USER);
            //User has to be saved to generate id
            userRoleRepository.save(userRole);
            return user;
        }
    }

    public String loginUser(User user) throws Exception{
        //check if username exists in user table
        User loggedUser=userRepository.findByUsername(user.getUsername());

        if(loggedUser==null){
            throw new Exception("Username does not exist in user table.");
        }
        if(PasswordUtils.verifyPassword(user.getPassword(),loggedUser.getPassword())){
            //if the password is correct
            return  jwtUtil.generateToken(UserDetails.builder().
                    username(user.getUsername()).
                    roles(this.findRoleByUserId(loggedUser.getId())).
                    build());
        }
        else{
            //password is wrong
            return null;
        }
    }

    public List<Role> findRoleByUserId(Long id ) throws RuntimeException{
        //username have to exist
        List<UserRole> userRoleList = redisCacheManager.get("user_roles:" + id, 600L,
                () -> userRoleRepository.findByUserId(id)
        );
        //username does not exist in user_role
        if(userRoleList==null || userRoleList.isEmpty()){
            throw new RuntimeException("User does not exist in UserRole for id = "+id);
        }
        else{
            return userRoleList.stream().map(UserRole::getRole).collect(Collectors.toList());
        }
    }
}
//caching and poor coding fix , implement auth in other microservices