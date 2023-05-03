package dnf.module.auction.controller;

import dnf.module.auction.dto.AuctionParam;
import dnf.module.auction.service.NeopleApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private NeopleApiService neopleApiService;

    @GetMapping("/AuctionSearch")
    public Map<String, Object> AuctionSearch() {
        Map<String, Object> ret = neopleApiService.AuctionSearch();
        return ret;
    }

    @PostMapping("/AuctionSold")
    public Map<String, Object> AuctionSold(@RequestBody AuctionParam param) {
        Map<String, Object> ret = neopleApiService.AuctionSold(param);
        return ret;
    }

    @PostMapping("/AuctionItem")
    public Map<String, Object> AuctionItem(@RequestBody AuctionParam param) {
        Map<String, Object> ret = neopleApiService.AuctionItem(param);
        return ret;
    }

    @PostMapping("/searchText")
    public Map<String, Object> searchText(@RequestBody Map<String, Object> param) {
        Map<String, Object> ret = neopleApiService.searchText(param);
        return ret;
    }
}
