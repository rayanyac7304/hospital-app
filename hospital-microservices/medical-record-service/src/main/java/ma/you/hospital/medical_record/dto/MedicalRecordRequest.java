package ma.you.hospital.medical_record.dto;



import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MedicalRecordRequest {
    @NotNull
    private Long patientId;
    private String title;
}
