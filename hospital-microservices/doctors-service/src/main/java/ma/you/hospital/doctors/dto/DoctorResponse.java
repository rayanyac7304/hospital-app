package ma.you.hospital.doctors.dto;

import ma.you.hospital.doctors.domain.Gender;

import java.time.Instant;

public record DoctorResponse(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String specialty,
        Gender gender,
        String phone,
        String address,
        Instant createdAt,
        Instant updatedAt
) {}
