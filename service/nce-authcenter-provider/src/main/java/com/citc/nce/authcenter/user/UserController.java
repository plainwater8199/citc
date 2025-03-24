package com.citc.nce.authcenter.user;


import com.citc.nce.authcenter.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;


@RestController()
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/user/userStatusSyn")
    public void userStatusSyn() {
        userService.userStatusSyn();
    }

}
