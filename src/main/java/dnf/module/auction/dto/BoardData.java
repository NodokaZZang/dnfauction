package dnf.module.auction.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardData {
    private MultipartFile imgFile;
    private String title;
    private String content;
    private String regBy;
}
