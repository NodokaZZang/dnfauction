package dnf.module.auction.service;

import dnf.module.auction.dto.BoardData;
import dnf.module.auction.dto.CommentData;
import dnf.module.auction.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository;
    public Map<String, Object> summernoteImg(BoardData param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            String ImgFilePath = "D:\\img\\";
            String originalFilename = param.getImgFile().getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String randomFileName = UUID.randomUUID().toString() + "." + ext;
            String filePath = ImgFilePath + randomFileName;
            File copyFile = new File(filePath);
            param.getImgFile().transferTo(copyFile);

            String fileUrl = "/image/view/" + randomFileName;
            ret.put("url", fileUrl);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> register(BoardData param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;

            String username = userDetails.getUsername();
            param.setRegBy(username);

            boardRepository.register(param);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }

        return ret;
    }

    public Map<String, Object> listView(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Integer nowPage = Integer.parseInt(param.get("nowPage").toString());
            Map<String, Object> queryParam = new HashMap<>();
            queryParam.put("offset", nowPage * 10);
            List<Map<String, Object>> list = boardRepository.boardList(queryParam);
            Integer integer = boardRepository.boardListCount();

            ret.put("listCount", integer / 10);
            ret.put("list", list);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> detail(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Integer sq = Integer.parseInt(param.get("sq").toString());

            Map<String, Object> board = boardRepository.findBySq(sq);

            // 조회수 올리기
            boardRepository.updateViewCount(sq);

            ret.put("data", board);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> vote(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Integer sq = Integer.parseInt(param.get("sq").toString());
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;
            String userId = userDetails.getUsername();

            Map<String, Object> condition = new HashMap<>();
            condition.put("sq", sq);
            condition.put("userId", userId);

            if (boardRepository.voteAble(condition) != null)
            {
                ret.put("result", false);
                ret.put("message", "이미 추천 하였습니다");
                return ret;
            }

            Map<String, Object> vote = boardRepository.vote(sq);
            ret.put("data", vote);
            ret.put("result", true);
        }
        catch (DuplicateKeyException de) {
            ret.put("result", false);
            ret.put("message", "이미 추천 하였습니다");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
            ret.put("message", "추천 에러");
        }
        return ret;
    }

    public Map<String, Object> modify(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            boardRepository.modify(param);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> delete(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            boardRepository.deleteBoard(param);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> commentRegister(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;
            String userId = userDetails.getUsername();

            param.put("regBy", userId);
            boardRepository.commentRegister(param);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> commentList(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            List<CommentData> commentDataList = boardRepository.commentList(param);
            List<CommentData> resultList= new ArrayList<>();
            Map<Long, CommentData> commentDataMap = new HashMap<>();

            for (var comment : commentDataList)
            {
                commentDataMap.put(comment.getSq(), comment);
                if (comment.getCommentParent() == 0)
                    resultList.add(comment);
            }


            for (var comment : commentDataList)
            {
                if (comment.getCommentParent() != 0)
                {
                    Long parentCommentSq = comment.getCommentParent();
                    CommentData parentComment = commentDataMap.get(parentCommentSq);

                    if (parentComment != null)
                        parentComment.getChildren().add(comment);
                }
            }

            ret.put("list", resultList);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> ccommentRegister(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;
            String userId = userDetails.getUsername();

            param.put("regBy", userId);
            boardRepository.ccommentRegister(param);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> deleteComment(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            boardRepository.deleteComment(param);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> ModifyCComment(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            boardRepository.ModifyCComment(param);
            ret.put("result", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> mylistView(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try
        {
            Integer nowPage = Integer.parseInt(param.get("nowPage").toString());
            Map<String, Object> queryParam = new HashMap<>();
            queryParam.put("offset", nowPage * 10);


            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;
            String userId = userDetails.getUsername();
            queryParam.put("userId", userId);

            List<Map<String, Object>> list = boardRepository.mylistView(queryParam);
            Integer integer = boardRepository.mylistViewCount(queryParam);

            ret.put("listCount", integer / 10);
            ret.put("list", list);
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
