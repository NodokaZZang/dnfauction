package dnf.module.auction.repository;

import dnf.module.auction.dto.UserInfo;
import dnf.module.auction.dto.UserProfile;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserRepository {
    UserInfo getUserInfo(String insertedId);

    void saveAuthKey(Map<String, String> authData);

    Map<String, Object> findAuthCode(Map<String, Object> param);

    void saveUser(Map<String, Object> param);

    void profileChange(UserProfile param);

    Map<String, Object> findUserByUserId(String userId);
}
