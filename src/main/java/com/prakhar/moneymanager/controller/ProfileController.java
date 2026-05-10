package com.prakhar.moneymanager.controller;

import com.prakhar.moneymanager.dto.AuthDTO;
import com.prakhar.moneymanager.dto.ProfileDTO;
import com.prakhar.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ProfileController {


    private  final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO>  regidterProfile(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile= profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }


    @GetMapping("/activate")
    public  ResponseEntity<String> activateProfile(@RequestParam  String token) {
        boolean activated = profileService.activateProfile(token);
        if (activated) {
            return ResponseEntity.ok("Profile activated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation token");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            if (!profileService.isAcountActive(authDTO.getEmail())) {
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account is not activated. Please check your email for activation instructions."));
            }
            Map<String , Object> response = profileService.authernticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST ).body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/test")
    public  String test(){
        return  "test Successful";
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile(){
        ProfileDTO profileDTO = profileService.getPublicProfile(null);
        return ResponseEntity.ok(profileDTO);
    }
}
