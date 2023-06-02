package dnf.module.auction.controller;

import dnf.module.auction.dto.UserProfile;
import dnf.module.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/emailCheckSend")
    public Map<String, Object> emailCheckSend(@RequestBody Map<String, Object> param)
    {
        return userService.emailCheckSend(param);
    }

    @PostMapping("/signUp")
    public Map<String, Object> signUp(@RequestBody Map<String, Object> param)
    {
        return userService.signUp(param);
    }

    @GetMapping("/userInfo")
    public Map<String, Object> userInfo()
    {
        return userService.userInfo();
    }

    @PostMapping(value = "/profileChange", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map<String, Object> profileChange(@ModelAttribute UserProfile param)
    {
        return userService.profileChange(param);
    }
}
