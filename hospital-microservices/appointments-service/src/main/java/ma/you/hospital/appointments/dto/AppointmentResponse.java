package ma.you.hospital.appointments.dto;
import lombok.Data;

@Data
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private String date;
    private String time;
    private String status;
}
