package ma.you.hospital.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "patient-service", url = "http://localhost:8083")
public interface PatientServiceClient {
    @PostMapping("/api/patients/from-user")
    void createPatientFromUser(@RequestBody Map<String, Object> request);
    @DeleteMapping("/api/patients/by-user/{userId}")
    void deletePatientByUserId(@PathVariable Long userId);
}