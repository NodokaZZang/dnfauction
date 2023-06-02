package dnf.module.auction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {
    @RequestMapping("/")
    public String homeView() {
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("/login");
        if (error != null)
        {
            mv.addObject("errorMsg", "Y");
        }
        else
        {
            mv.addObject("errorMsg", "N");
        }
        return mv;
    }

}
