package pl.monify.user.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUserInfo() {
        return "User info from UserService";
    }
}
