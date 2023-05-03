package dnf.module.auction.repository;

import dnf.module.auction.dto.AuctionItemData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuctionItemRepository {
    void auctionItemInsert(AuctionItemData param);

    List<Map<String, Object>> auctionSearch();
}
