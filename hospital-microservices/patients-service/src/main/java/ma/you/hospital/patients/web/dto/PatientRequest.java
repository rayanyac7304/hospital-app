package ma.you.hospital.patients.web.dto;

import ma.you.hospital.patients.domain.Gender;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PatientRequest(
        @NotBlank @Size(max = 60) String firstName,
        @NotBlank @Size(max = 60) String lastName,
        Gender gender,
        LocalDate birthDate,
        @Size(max = 30) String phone,
        @Size(max = 255) String address
) {}
