package ma.you.hospital.appointments.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.you.hospital.appointments.clients.DoctorRestClient;
import ma.you.hospital.appointments.clients.PatientRestClient;
import ma.you.hospital.appointments.dto.AppointmentDetailResponse;
import ma.you.hospital.appointments.dto.AppointmentRequest;
import ma.you.hospital.appointments.domain.Appointment;
import ma.you.hospital.appointments.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import feign.FeignException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRestClient doctorRestClient;
    private final PatientRestClient patientRestClient;

    public List<AppointmentDetailResponse> getAllWithDetails(LocalDate filterDate) {
        List<Appointment> appointments;

        if (filterDate != null) {
            appointments = appointmentRepository.findByDate(filterDate);
        } else {
            appointments = appointmentRepository.findAll();
        }

        return appointments.stream()
                .map(this::toDetailResponse)
                .filter(Objects::nonNull)
                .toList();
    }

    private AppointmentDetailResponse toDetailResponse(Appointment appointment) {
        try {
            // Fetch patient and doctor details
            Map<String, Object> patient = patientRestClient.getPatientById(appointment.getPatientId());
            Map<String, Object> doctor = doctorRestClient.getDoctorById(appointment.getDoctorId());

            // Extract names from Map
            String patientName = patient.get("firstName") + " " + patient.get("lastName");
            String doctorName = doctor.get("firstName") + " " + doctor.get("lastName");

            return AppointmentDetailResponse.builder()
                    .id(appointment.getId())
                    .doctorId(appointment.getDoctorId())
                    .doctorName(doctorName)
                    .patientId(appointment.getPatientId())
                    .patientName(patientName)
                    .date(appointment.getDate().toString())
                    .time(appointment.getTime().toString())
                    .status(appointment.getStatus())
                    .build();
        } catch (FeignException.NotFound e) {
            log.warn("Patient or Doctor not found for appointment ID {}: patientId={}, doctorId={}",
                    appointment.getId(), appointment.getPatientId(), appointment.getDoctorId());
            return null;
        } catch (FeignException e) {
            log.error("Error fetching patient/doctor details for appointment {}: {}",
                    appointment.getId(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error processing appointment {}: {}",
                    appointment.getId(), e.getMessage());
            return null;
        }
    }

    public Appointment createAppointment(AppointmentRequest request) {
        patientRestClient.getPatientById(request.getPatientId());
        doctorRestClient.getDoctorById(request.getDoctorId());

        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime time = LocalTime.parse(request.getTime());

        if (appointmentRepository.existsByDoctorAndDateTime(request.getDoctorId(), date, time)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This time slot is already booked for the selected doctor"
            );
        }

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
        Appointment existing = getById(id);

        if (!existing.getPatientId().equals(request.getPatientId())) {
            patientRestClient.getPatientById(request.getPatientId());
        }

        if (!existing.getDoctorId().equals(request.getDoctorId())) {
            doctorRestClient.getDoctorById(request.getDoctorId());
        }

        LocalDate newDate = LocalDate.parse(request.getDate());
        LocalTime newTime = LocalTime.parse(request.getTime());

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

    public List<Appointment> getByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }
}