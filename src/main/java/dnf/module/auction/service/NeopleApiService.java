package dnf.module.auction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dnf.module.auction.dto.AuctionData;
import dnf.module.auction.dto.AuctionItemData;
import dnf.module.auction.dto.AuctionParam;
import dnf.module.auction.repository.AuctionItemRepository;
import dnf.module.auction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NeopleApiService {

    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private AuctionItemRepository auctionItemRepository;
    private String API_KEY = "p25FLD40Df0QiLxGxJ4jJR4XWCZfnpfv";

    private String API_HOST = "https://api.neople.co.kr/";

    private ObjectMapper objectMapper = new ObjectMapper();

    /*
        경매장 시세조회
     */
    public Map<String, Object> AuctionSold(AuctionParam param) {
        Map<String, Object> ret = new HashMap<>();
        try {
            String URL = API_HOST + "df/auction-sold?itemName="
                    + URLEncoder.encode(param.getItemName(), StandardCharsets.UTF_8)
                    + "&wordType=front&wordShort=true&limit=100&apikey="
                    + API_KEY;

            HttpResponse<String> res = Unirest.get(URL)
                    .asString();

            if (res.getStatus() != 200) {
                ret.put("result", false);
                return ret;
            }

            String responseBody = res.getBody();
            Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);

            Map<String, Map<String, AuctionData>> upperSearchMap = new HashMap<>();

            List<Map<String, Object>> list = (List<Map<String, Object>>) jsonMap.get("rows");
            Map<String, Integer> countMap = new HashMap<>();

            SimpleDateFormat yyyymmddhhmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyy-MM-dd");

            list.forEach((item) -> {
                String itemId = (String) item.get("itemId");
                String itemName = (String) item.get("itemName");
                Integer unitPrice = (Integer) item.get("unitPrice");
                String soldDate = (String) item.get("soldDate");
                String pSoldDate = null;

                try {
                    Date DateSoldDate = yyyymmddhhmmss.parse(soldDate);
                    pSoldDate = yyyymmdd.format(DateSoldDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                AuctionData auctionData = new AuctionData();
                auctionData.setItemId(itemId);
                auctionData.setItemName(itemName);
                auctionData.setUnitPrice(unitPrice);
                auctionData.setUnitPriceL(unitPrice);
                auctionData.setSoldDate(pSoldDate);

                countMap.put(itemId, countMap.getOrDefault(itemId, 0) + 1);

                if (upperSearchMap.get(itemId) != null) {
                    Map<String, AuctionData> auctionDataMap = upperSearchMap.get(itemId);

                    if (auctionDataMap.get(auctionData.getSoldDate()) == null) {
                        auctionDataMap.put(auctionData.getSoldDate(), auctionData);
                    } else {
                        if (auctionDataMap.get(auctionData.getSoldDate()).getUnitPrice() < auctionData.getUnitPrice()) {
                            auctionDataMap.get(auctionData.getSoldDate()).setUnitPrice(auctionData.getUnitPrice());
                        }

                        if (auctionDataMap.get(auctionData.getSoldDate()).getUnitPriceL() == 0
                                || auctionDataMap.get(auctionData.getSoldDate()).getUnitPriceL() > auctionData.getUnitPrice()) {
                            auctionDataMap.get(auctionData.getSoldDate()).setUnitPriceL(auctionData.getUnitPrice());
                        }
                    }
                } else {
                    Map<String, AuctionData> newAuctionData = new HashMap<>();
                    newAuctionData.put(auctionData.getSoldDate(), auctionData);
                    upperSearchMap.put(itemId, newAuctionData);
                }
            });

            upperSearchMap.forEach((k, v) -> {
                v.forEach((key, value) -> {
                    auctionRepository.auctionDataInsert(value);
                });
            });

            int count = -1;
            String rankItemId = null;

            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                String k = entry.getKey();
                Integer v = entry.getValue();
                if (count < v) {
                    count = v;
                    rankItemId = k;
                }
            }

            List<AuctionData> auctionDataList = auctionRepository.auctionDataSelectTop10(rankItemId);

            ret.put("auctionDataList", auctionDataList);
            ret.put("data", jsonMap);
            ret.put("result", true);

        } catch (Exception e) {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> searchText(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<>();
        try {
            List<String> datas = auctionRepository.auctionItemNameList(param);
            ret.put("result", true);
            ret.put("data", datas);
        }
        catch (Exception e) {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> AuctionItem(AuctionParam param) {
        Map<String, Object> ret = new HashMap<>();
        try {
            String URL = API_HOST + "df/auction?itemName="
                    + URLEncoder.encode(param.getItemName(), StandardCharsets.UTF_8)
                    + "&wordType=front&wordShort=true&limit=100&apikey="
                    + API_KEY;

            HttpResponse<String> res = Unirest.get(URL)
                    .asString();

            if (res.getStatus() != 200) {
                ret.put("result", false);
                return ret;
            }

            String responseBody = res.getBody();
            Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);

            Map<String, Map<String, AuctionData>> upperSearchMap = new HashMap<>();

            List<Map<String, Object>> list = (List<Map<String, Object>>) jsonMap.get("rows");
            SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyy-MM-dd");

            Map<String, AuctionItemData> auctionItemDataMap = new HashMap<>();

            list.forEach((item) -> {
                String itemId = (String) item.get("itemId");
                String itemName = (String) item.get("itemName");

                Date date = new Date(System.currentTimeMillis());
                String regDate = yyyymmdd.format(date);

                AuctionItemData auctionData = new AuctionItemData();
                auctionData.setItemId(itemId);
                auctionData.setItemName(itemName);
                auctionData.setRegDt(regDate);

                auctionItemDataMap.put(itemId, auctionData);
            });

            auctionItemDataMap.forEach((k,v)-> {
                auctionItemRepository.auctionItemInsert(v);
            });

            ret.put("data", jsonMap);
            ret.put("result", true);
        }
        catch (Exception e) {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }

    public Map<String, Object> AuctionSearch() {
        Map<String, Object> ret = new HashMap<>();
        try {
            List<Map<String, Object>> datas = auctionItemRepository.auctionSearch();
            ret.put("result", true);
            ret.put("data", datas);
        }
        catch (Exception e) {
            e.printStackTrace();
            ret.put("result", false);
        }
        return ret;
    }
}
