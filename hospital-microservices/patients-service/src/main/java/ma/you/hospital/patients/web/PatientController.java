package ma.you.hospital.patients.web;

import ma.you.hospital.patients.domain.Gender;
import ma.you.hospital.patients.domain.Patient;
import ma.you.hospital.patients.services.PatientService;
import ma.you.hospital.patients.web.dto.PatientRequest;
import ma.you.hospital.patients.web.dto.PatientResponse;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PatientResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]);
        if (sort.endsWith(",desc")) s = s.descending();
        else s = s.ascending();
        Pageable pageable = PageRequest.of(page, size, s);
        return service.list(q, pageable).map(PatientMapper::toResponse);
    }

    @GetMapping("/{id}")
    public PatientResponse get(@PathVariable Long id) {
        return PatientMapper.toResponse(service.get(id));
    }

    // ✅ Main endpoint - used by frontend
    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest req) {
        System.out.println("📥 Creating patient from frontend");
        System.out.println("   - firstName: " + req.firstName());


        Patient patient = PatientMapper.toEntity(req, null);
        patient.setUserId(null);  // ← Frontend patients don't have userId

        Patient saved = service.create(patient);
        System.out.println("✅ Patient created: " + saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PatientMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable Long id, @Valid @RequestBody PatientRequest req) {
        var updated = service.update(id, p -> PatientMapper.update(p, req));
        return PatientMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ✅ Internal endpoint - used by Auth Service via Feign
    @PostMapping("/from-user")
    public ResponseEntity<PatientResponse> createFromUser(@RequestBody Map<String, Object> req) {
        System.out.println("📥 Creating patient from Auth Service");
        System.out.println("   - userId: " + req.get("userId"));
        System.out.println("   - firstName: " + req.get("firstName"));

        Patient patient = new Patient();
        patient.setUserId(((Number) req.get("userId")).longValue());  // ← Auth patients have userId
        patient.setFirstName((String) req.get("firstName"));
        patient.setLastName((String) req.get("lastName"));
        patient.setGender(Gender.valueOf((String) req.get("gender")));
        patient.setBirthDate(LocalDate.parse((String) req.get("birthDate")));
        patient.setPhone((String) req.get("phone"));
        patient.setAddress((String) req.get("address"));

        Patient saved = service.create(patient);
        System.out.println("✅ Patient created with userId: " + saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PatientMapper.toResponse(saved));
    }

}