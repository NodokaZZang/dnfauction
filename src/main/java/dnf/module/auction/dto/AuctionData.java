package dnf.module.auction.dto;

import lombok.Data;

@Data
public class AuctionData {
    private String itemName;
    private String itemId;
    private int unitPrice = 0;
    private String soldDate;
    private int unitPriceL = 0;
}
