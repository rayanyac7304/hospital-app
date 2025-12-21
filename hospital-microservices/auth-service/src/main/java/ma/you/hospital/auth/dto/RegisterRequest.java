package ma.you.hospital.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String role; // DOCTOR, PATIENT

    // Patient-specific fields (optional, only for PATIENT role)
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate; // ISO format: "2000-01-15"
    private String phone;
    private String address;
}