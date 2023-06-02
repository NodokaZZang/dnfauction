package dnf.module.auction.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserProfile {
    private MultipartFile profileImg;
    private String profileImgFileName;
    private String userId;
}
