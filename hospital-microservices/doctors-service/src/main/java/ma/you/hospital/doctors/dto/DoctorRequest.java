package ma.you.hospital.doctors.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ma.you.hospital.doctors.domain.Gender;


public record DoctorRequest(
         String firstName,
        String lastName,
         String specialty,
         Gender gender,
        String phone,
        String address
) {}

