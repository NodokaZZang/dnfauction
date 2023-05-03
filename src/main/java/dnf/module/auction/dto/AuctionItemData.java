package dnf.module.auction.dto;

import lombok.Data;

@Data
public class AuctionItemData {
    private String itemName;
    private String itemId;
    private String regDt;
    private Integer searchCnt;
}
