package ma.you.hospital.doctors.web;

import ma.you.hospital.doctors.domain.Doctor;
import ma.you.hospital.doctors.dto.DoctorRequest;
import ma.you.hospital.doctors.dto.DoctorResponse;

public class DoctorMapper {

    public static Doctor toEntity(DoctorRequest r, Long userId) {
        var d = new Doctor();
        d.setUserId(userId);
        d.setFirstName(r.firstName());
        d.setLastName(r.lastName());
        d.setSpecialty(r.specialty());
        d.setPhone(r.phone());
        d.setAddress(r.address());
        d.setGender(r.gender());
        return d;
    }

    public static void update(Doctor d, DoctorRequest r) {
        d.setFirstName(r.firstName());
        d.setLastName(r.lastName());
        d.setSpecialty(r.specialty());
        d.setPhone(r.phone());
        d.setAddress(r.address());
        d.setGender(r.gender());
    }

    public static DoctorResponse toResponse(Doctor d) {
        return new DoctorResponse(
                d.getId(),
                d.getUserId(),
                d.getFirstName(),
                d.getLastName(),
                d.getSpecialty(),
                d.getGender(),
                d.getPhone(),
                d.getAddress(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }
}
