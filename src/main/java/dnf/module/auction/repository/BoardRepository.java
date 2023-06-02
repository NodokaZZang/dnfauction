package dnf.module.auction.repository;

import dnf.module.auction.dto.BoardData;
import dnf.module.auction.dto.CommentData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardRepository {
    void register(BoardData param);
    List<Map<String, Object>> boardList(Map<String, Object> param);

    Integer boardListCount();

    Map<String, Object> findBySq(Integer sq);

    void updateViewCount(Integer sq);

    Map<String, Object> voteAble(Map<String, Object> condition);

    Map<String, Object> vote(Integer sq);

    void modify(Map<String, Object> param);

    void deleteBoard(Map<String, Object> param);

    void commentRegister(Map<String, Object> param);

    List<CommentData> commentList(Map<String, Object> param);

    void ccommentRegister(Map<String, Object> param);

    void deleteComment(Map<String, Object> param);

    void ModifyCComment(Map<String, Object> param);

    List<Map<String, Object>> mylistView(Map<String, Object> queryParam);

    Integer mylistViewCount(Map<String, Object> queryParam);

    List<Map<String, Object>> mylistViewAll(String userId);
}
