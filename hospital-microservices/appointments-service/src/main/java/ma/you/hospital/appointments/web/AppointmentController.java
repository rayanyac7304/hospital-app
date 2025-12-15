package ma.you.hospital.appointments.web;

import lombok.RequiredArgsConstructor;
import ma.you.hospital.appointments.dto.AppointmentRequest;
import ma.you.hospital.appointments.domain.Appointment;
import ma.you.hospital.appointments.services.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:4200")  // ← Add CORS
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@RequestBody AppointmentRequest request) {
        return appointmentService.createAppointment(request);
    }

    @GetMapping
    public List<Appointment> all() {
        return appointmentService.getAll();
    }

    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id) {
        return appointmentService.getById(id);
    }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable Long id, @RequestBody AppointmentRequest request) {
        return appointmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        appointmentService.delete(id);
    }

    @PatchMapping("/{id}/cancel")
    public Appointment cancel(@PathVariable Long id) {
        return appointmentService.cancel(id);
    }

    // Optional filtering endpoints
    @GetMapping("/patient/{patientId}")
    public List<Appointment> getByPatient(@PathVariable Long patientId) {
        return appointmentService.getByPatientId(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getByDoctorId(doctorId);
    }

    @GetMapping("/date/{date}")
    public List<Appointment> getByDate(@PathVariable String date) {
        return appointmentService.getByDate(LocalDate.parse(date));
    }
}