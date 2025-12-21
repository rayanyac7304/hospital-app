package ma.you.hospital.patients.web;

import ma.you.hospital.patients.domain.Patient;
import ma.you.hospital.patients.web.dto.*;

public class PatientMapper {

    public static Patient toEntity(PatientRequest r, Long userId) {
        var p = new Patient();
        p.setUserId(userId);
        p.setFirstName(r.firstName());
        p.setLastName(r.lastName());
        p.setGender(r.gender());
        p.setBirthDate(r.birthDate());
        p.setPhone(r.phone());
        p.setAddress(r.address());
        return p;
    }

    public static void update(Patient p, PatientRequest r) {
        p.setFirstName(r.firstName());
        p.setLastName(r.lastName());
        p.setGender(r.gender());
        p.setBirthDate(r.birthDate());
        p.setPhone(r.phone());
        p.setAddress(r.address());
    }

    public static PatientResponse toResponse(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getUserId(),
                p.getFirstName(),
                p.getLastName(),
                p.getGender(),
                p.getBirthDate(),
                p.getPhone(),
                p.getAddress(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
