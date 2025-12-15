package ma.you.hospital.appointments.services;

import lombok.RequiredArgsConstructor;
import ma.you.hospital.appointments.clients.DoctorRestClient;
import ma.you.hospital.appointments.clients.PatientRestClient;
import ma.you.hospital.appointments.dto.AppointmentRequest;
import ma.you.hospital.appointments.domain.Appointment;
import ma.you.hospital.appointments.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRestClient doctorRestClient;
    private final PatientRestClient patientRestClient;

    public Appointment createAppointment(AppointmentRequest request) {
        // Verify patient exists
        patientRestClient.getPatientById(request.getPatientId());

        // Verify doctor exists
        doctorRestClient.getDoctorById(request.getDoctorId());

        // Parse date and time
        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime time = LocalTime.parse(request.getTime());

        // Check if time slot is available
        if (appointmentRepository.existsByDoctorAndDateTime(request.getDoctorId(), date, time)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This time slot is already booked for the selected doctor"
            );
        }

        // Create appointment
        Appointment appointment = Appointment.builder()
                .doctorId(request.getDoctorId())
                .patientId(request.getPatientId())
                .date(date)
                .time(time)
                .status("CONFIRMED")
                .build();

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Appointment not found with id " + id
                        )
                );
    }

    public Appointment update(Long id, AppointmentRequest request) {
        // Get existing appointment
        Appointment existing = getById(id);

        // Verify patient exists (if changed)
        if (!existing.getPatientId().equals(request.getPatientId())) {
            patientRestClient.getPatientById(request.getPatientId());
        }

        // Verify doctor exists (if changed)
        if (!existing.getDoctorId().equals(request.getDoctorId())) {
            doctorRestClient.getDoctorById(request.getDoctorId());
        }

        // Parse new date and time
        LocalDate newDate = LocalDate.parse(request.getDate());
        LocalTime newTime = LocalTime.parse(request.getTime());

        // Check if time slot changed and if new slot is available
        boolean dateTimeChanged = !existing.getDate().equals(newDate) ||
                !existing.getTime().equals(newTime) ||
                !existing.getDoctorId().equals(request.getDoctorId());

        if (dateTimeChanged && appointmentRepository.existsByDoctorAndDateTime(
                request.getDoctorId(), newDate, newTime)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This time slot is already booked for the selected doctor"
            );
        }

        // Update fields
        existing.setDoctorId(request.getDoctorId());
        existing.setPatientId(request.getPatientId());
        existing.setDate(newDate);
        existing.setTime(newTime);

        return appointmentRepository.save(existing);
    }

    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Appointment not found with id " + id
            );
        }
        appointmentRepository.deleteById(id);
    }

    public Appointment cancel(Long id) {
        Appointment appointment = getById(id);

        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment is already cancelled"
            );
        }

        appointment.setStatus("CANCELLED");
        return appointmentRepository.save(appointment);
    }

    // Optional: Get appointments by patient
    public List<Appointment> getByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    // Optional: Get appointments by doctor
    public List<Appointment> getByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    // Optional: Get appointments by date
    public List<Appointment> getByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }
}