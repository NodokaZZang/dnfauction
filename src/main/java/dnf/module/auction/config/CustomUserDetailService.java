package dnf.module.auction.config;

import dnf.module.auction.dto.UserInfo;
import dnf.module.auction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String insertedId) throws UsernameNotFoundException {
        UserInfo user = userRepository.getUserInfo(insertedId);

        if (user == null) {
            return null;
        }

        String pw = user.getPw(); //"d404559f602eab6fd602ac7680dacbfaadd13630335e951f097af3900e9de176b6db28512f2e000b9d04fba5133e8b1c6e8df59db3a8ab9d60be4b97cc9e81db"
        String roles = user.getRoles(); //"USER"

        return User.builder()
                .username(insertedId)
                .password(pw)
                .roles(roles)
                .build();
    }
}
