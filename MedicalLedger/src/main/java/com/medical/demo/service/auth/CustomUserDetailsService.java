package com.medical.demo.service.auth;
import com.medical.demo.model.User; import com.medical.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor; import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with email: "+email));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPasswordHash(),
                user.getIsActive()!=null&&user.getIsActive(),true,true,true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())));
    }
}