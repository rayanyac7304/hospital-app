package ma.you.hospital.medical_record.service;

import lombok.RequiredArgsConstructor;
import ma.you.hospital.medical_record.domain.MedicalRecord;
import ma.you.hospital.medical_record.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository repository;

    public MedicalRecord create(Long patientId, String title) {

        MedicalRecord record = MedicalRecord.builder()
                .patientId(patientId)
                .title(title)
                .archived(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return repository.save(record);
    }

    public MedicalRecord getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MedicalRecord not found"));
    }

    public List<MedicalRecord> getByPatient(Long patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<MedicalRecord> getAllActive() {
        return repository.findByArchivedFalse();
    }

    public void archive(Long id) {
        MedicalRecord record = getById(id);
        record.setArchived(true);
        record.setArchivedAt(LocalDate.now());
        repository.save(record);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
