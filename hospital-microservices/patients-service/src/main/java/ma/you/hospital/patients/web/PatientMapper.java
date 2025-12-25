package ma.you.hospital.patients.web;

import ma.you.hospital.patients.domain.Patient;
import ma.you.hospital.patients.web.dto.PatientRequest;
import ma.you.hospital.patients.web.dto.PatientResponse;

public class PatientMapper {

    public static Patient toEntity(PatientRequest req, Long userId) {
        Patient patient = new Patient();
        patient.setUserId(userId);
        patient.setFirstName(req.firstName());
        patient.setLastName(req.lastName());
        patient.setGender(req.gender());
        patient.setBirthDate(req.birthDate());
        patient.setPhone(req.phone());
        patient.setAddress(req.address());
        return patient;
    }

    public static PatientResponse toResponse(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getUserId(),           // ← ADD THIS
                p.getFirstName(),
                p.getLastName(),
                p.getGender(),
                p.getBirthDate(),
                p.getPhone(),
                p.getAddress(),
                p.getCreatedAt(),        // ← ADD THIS
                p.getUpdatedAt()         // ← ADD THIS
        );
    }

    public static void update(Patient p, PatientRequest req) {
        p.setFirstName(req.firstName());
        p.setLastName(req.lastName());
        p.setGender(req.gender());
        p.setBirthDate(req.birthDate());
        p.setPhone(req.phone());
        p.setAddress(req.address());
    }
}