package vn.edu.crs.registration_service.controller;

import vn.edu.crs.registration_service.dto.RegistrationRequestDTO;
import vn.edu.crs.registration_service.entity.Registration;
import vn.edu.crs.registration_service.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Registration register(@Valid @RequestBody RegistrationRequestDTO dto) {
        return registrationService.register(dto);
    }

    @GetMapping
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    @GetMapping("/my")
    public List<Registration> getMyRegistrations(Authentication authentication) {
        Object credentials = authentication.getCredentials();
        Long studentId = Long.valueOf(credentials.toString());

        return registrationService.getMyRegistrations(studentId);
    }
    @DeleteMapping("/{id}")
    public void cancel(@PathVariable Long id) {
        registrationService.cancel(id);
    }
}