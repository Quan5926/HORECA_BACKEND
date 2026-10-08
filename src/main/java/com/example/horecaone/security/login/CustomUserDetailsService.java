package com.example.horecaone.security.login;

import com.example.horecaone.entity.login.NguoiDung;
import com.example.horecaone.repository.login.NguoiDungRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    public CustomUserDetailsService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        NguoiDung user = nguoiDungRepository.findByEmailOrSoDienThoai(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + identifier));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getMatKhauHash())
                .authorities(user.getAuthorities())
                .accountExpired(false)
                .accountLocked(!user.getTrangThai())
                .credentialsExpired(false)
                .disabled(!user.getTrangThai())
                .build();
    }
}