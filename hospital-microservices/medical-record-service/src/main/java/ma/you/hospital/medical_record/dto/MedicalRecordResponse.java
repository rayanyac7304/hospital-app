package ma.you.hospital.medical_record.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MedicalRecordResponse {

    private Long id;
    private Long patientId;
    private LocalDate createdAt;
    private boolean archived;
    private List<String> files;
}
