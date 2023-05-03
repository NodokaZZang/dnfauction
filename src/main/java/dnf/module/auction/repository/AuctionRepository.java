package dnf.module.auction.repository;

import dnf.module.auction.dto.AuctionData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuctionRepository {
    void auctionDataInsert(AuctionData auctionData);

    List<AuctionData> auctionDataSelectTop10(String itemId);

    List<String> auctionItemNameList(Map<String, Object> itemMap);
}
