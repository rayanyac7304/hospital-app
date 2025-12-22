package ma.you.hospital.medical_record.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.you.hospital.medical_record.domain.MedicalRecord;
import ma.you.hospital.medical_record.dto.MedicalRecordRequest;
import ma.you.hospital.medical_record.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MedicalRecordController {

    private final MedicalRecordService service;

    @PostMapping
    public ResponseEntity<MedicalRecord> create(
            @RequestBody @Valid MedicalRecordRequest request
    ) {
        MedicalRecord record = service.create(
                request.getPatientId(),
                request.getTitle()
        );
        return ResponseEntity.ok(record);
    }


    @GetMapping("/{id}")
    public MedicalRecord getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecord> getByPatient(@PathVariable Long patientId) {
        return service.getByPatient(patientId);
    }

    @GetMapping
    public List<MedicalRecord> getAllActive() {
        return service.getAllActive();
    }


    @PostMapping("/{id}/archive")
    public void archive(@PathVariable Long id) {
        service.archive(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
