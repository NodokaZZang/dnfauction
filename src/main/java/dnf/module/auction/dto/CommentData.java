package dnf.module.auction.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentData {
    private Long sq;
    private String regBy;
    private String content;
    private Long boardSq;
    private Long commentParent;
    private List<CommentData> children = new ArrayList<>();
    private String userProfile;
    private String deleteYn;
}
