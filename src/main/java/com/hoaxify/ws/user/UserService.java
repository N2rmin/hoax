package com.hoaxify.ws.user;

import com.hoaxify.ws.error.ApiError;
import com.hoaxify.ws.error.NotFoundException;
import com.hoaxify.ws.file.FileService;
import com.hoaxify.ws.hoax.HoaxService;
import com.hoaxify.ws.shared.CurrentUser;
import com.hoaxify.ws.user.vm.UserUpdateVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;

@Service
public class UserService {

    UserRepository userRepository;


    FileService fileService;

    PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository , PasswordEncoder passwordEncoder, FileService fileService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService=fileService;
    }



    public  void save(User user) {

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Page<User> getUsers(Pageable page, User user) {

        if (user !=null){

            return  userRepository.findByUsernameNot(user.getUsername(),page);
        }
        return userRepository.findAll(page);
    }

    public User getByUsername(String username) {
        User inDB = userRepository.findByUsername(username);
        if (inDB ==null){
            throw new NotFoundException();
        }
        return inDB;
    }

    public User updateUser(String username, UserUpdateVM updateUser ) {

        User inDB=getByUsername(username);
        inDB.setDisplayName(updateUser.getDisplayName());
        if (updateUser.getImage()!=null){
            String oldImageName= inDB.getImage();
            try {
                String storedFileName= fileService.writeBase64EncodedStringToFile(updateUser.getImage());
                inDB.setImage(storedFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileService.deleteProfileImage(oldImageName);
        }
        return userRepository.save(inDB);
    }


    public void deleteUser(String username) {
        //hoaxService.deleteHoaxesOfUser(username);
       User inDB= userRepository.findByUsername(username);
        fileService.deleteAllStoredFilesForUser(inDB);
        userRepository.delete(inDB);

    }
}
