package dnf.module.auction.dto;

import lombok.Data;

@Data
public class AuctionParam {
    private Integer limit = 10;
    private String itemId;
    private String itemName;
    private String wordType;
    private boolean wordShort = true;
}
