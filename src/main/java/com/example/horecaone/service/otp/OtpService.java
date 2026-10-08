// service/otp/OtpService.java
package com.example.horecaone.service.otp;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private record OtpEntry(String code, Instant expiresAt) {}
    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();

    public String generateAndStore(String email) {
        String otp = String.format("%06d",
                new java.util.Random().nextInt(1_000_000));
        store.put(email.toLowerCase(),
                new OtpEntry(otp, Instant.now().plusSeconds(300))); // 5 phút
        return otp;
    }

    public boolean verify(String email, String otp) {
        OtpEntry entry = store.get(email.toLowerCase());
        if (entry == null) return false;
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(email.toLowerCase());
            return false;
        }
        if (!entry.code().equals(otp)) return false;
        store.remove(email.toLowerCase()); // dùng 1 lần rồi xóa
        return true;
    }
}