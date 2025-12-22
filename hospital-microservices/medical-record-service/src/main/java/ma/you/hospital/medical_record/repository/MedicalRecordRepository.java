package ma.you.hospital.medical_record.repository;


import ma.you.hospital.medical_record.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByArchivedFalse();
}

