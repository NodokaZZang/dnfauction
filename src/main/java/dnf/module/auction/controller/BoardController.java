package dnf.module.auction.controller;

import dnf.module.auction.dto.BoardData;
import dnf.module.auction.dto.UserProfile;
import dnf.module.auction.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardService boardService;
    @PostMapping(value = "/summernoteImg", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map<String, Object> summernoteImg(@ModelAttribute BoardData param) {return boardService.summernoteImg(param);}
    @PostMapping(value = "/register")
    public Map<String, Object> register(@RequestBody BoardData param)
    {
        return boardService.register(param);
    }
    @PostMapping(value = "/listView")
    public Map<String, Object> listView(@RequestBody Map<String, Object> param) {return boardService.listView(param);}
    @PostMapping(value = "/detail")
    public Map<String, Object> detail(@RequestBody Map<String, Object> param)
    {
        return boardService.detail(param);
    }
    @PostMapping(value = "/vote")
    public Map<String, Object> vote(@RequestBody Map<String, Object> param) {return boardService.vote(param);}
    @PostMapping(value = "modify")
    public Map<String, Object> modify(@RequestBody Map<String, Object> param)
    {
        return boardService.modify(param);
    }
    @PostMapping(value = "delete")
    public Map<String, Object> delete(@RequestBody Map<String, Object> param)
    {
        return boardService.delete(param);
    }
    @PostMapping(value = "commentRegister")
    public Map<String, Object> commentRegister(@RequestBody Map<String, Object> param)
    {
        return boardService.commentRegister(param);
    }
    @PostMapping(value = "commentList")
    public Map<String, Object> commentList(@RequestBody Map<String, Object> param)
    {
        return boardService.commentList(param);
    }
    @PostMapping(value = "ccommentRegister")
    public Map<String, Object> ccommentRegister(@RequestBody Map<String, Object> param)
    {
        return boardService.ccommentRegister(param);
    }
    @PostMapping(value = "deleteComment")
    public Map<String, Object> deleteComment(@RequestBody Map<String, Object> param)
    {
        return boardService.deleteComment(param);
    }
    @PostMapping(value = "ModifyCComment")
    public Map<String, Object> ModifyCComment(@RequestBody Map<String, Object> param)
    {
        return boardService.ModifyCComment(param);
    }

    @PostMapping("/mylistView")
    public Map<String, Object> mylistView(@RequestBody Map<String, Object> param)
    {
        return boardService.mylistView(param);
    }
}
