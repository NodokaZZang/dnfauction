package dnf.module.auction.service;

import dnf.module.auction.dto.UserProfile;
import dnf.module.auction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    JMailService jmailService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Map<String, Object> emailCheckSend(Map<String, Object> param)
    {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            String userEmail = param.get("useremail").toString();

            int authNo = (int)(Math.random() * (99999 - 10000 + 1)) + 10000;
            String context = "인증번호 보내드립니다 [ " + authNo + " ] ";
            jmailService.SendEmail(context,"던파옥션 인증번호 보내드립니다",userEmail);

            String AuthKey = UUID.randomUUID().toString();

            Map<String, String> authData=  new HashMap<>();
            authData.put("authKey", AuthKey);
            authData.put("authNo", String.valueOf(authNo));

            userRepository.saveAuthKey(authData);

            ret.put("authKey", AuthKey);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> signUp(Map<String, Object> param)
    {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Map<String, Object> authCode = userRepository.findAuthCode(param);

            if (authCode == null)
            {
                ret.put("reult", false);
                ret.put("message", "인증번호가 올바르지 않습니다");
                return ret;
            }

            param.put("userPw",passwordEncoder.encode(param.get("userPw").toString()));
            userRepository.saveUser(param);

            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
            ret.put("message", "회원가입 실패");
        }
        return ret;
    }

    public Map<String, Object> userInfo() {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;
            String username = userDetails.getUsername();
            Map<String, Object> user = userRepository.findUserByUserId(username);
            user.put("username", username);
            ret.put("data", user);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
            ret.put("message", "회원가입 실패");
        }
        return ret;
    }

    public Map<String, Object> profileChange(UserProfile param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            String ImgFilePath = "D:\\img\\";
            String originalFilename = param.getProfileImg().getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String randomFileName = UUID.randomUUID().toString() + "." + ext;
            String filePath = ImgFilePath + randomFileName;
            File copyFile = new File(filePath);
            param.getProfileImg().transferTo(copyFile);
            param.setProfileImgFileName("/image/view/" + randomFileName);
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;
            String username = userDetails.getUsername();
            param.setUserId(username);

            userRepository.profileChange(param);

            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }
}
