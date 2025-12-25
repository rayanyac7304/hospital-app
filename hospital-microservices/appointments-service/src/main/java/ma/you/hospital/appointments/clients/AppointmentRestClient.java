package ma.you.hospital.appointments.clients;

import ma.you.hospital.appointments.domain.Appointment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "appointments-service")
public interface AppointmentRestClient {

    @GetMapping("/api/appointments/{id}")
    Appointment getAppointmentById(@PathVariable("id") Long id);
}

