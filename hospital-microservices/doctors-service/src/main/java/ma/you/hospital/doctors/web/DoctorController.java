package ma.you.hospital.doctors.web;

import jakarta.validation.Valid;
import ma.you.hospital.doctors.domain.Doctor;
import ma.you.hospital.doctors.service.DoctorService;
import ma.you.hospital.doctors.dto.DoctorRequest;
import ma.you.hospital.doctors.dto.DoctorResponse;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:4200")
public class DoctorController {

    private final DoctorService service;

    public DoctorController(DoctorService service) {
        this.service = service;
    }

    /* ============================
       LIST (with search)
       ============================ */
    @GetMapping
    public Page<DoctorResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]);
        if (sort.endsWith(",desc")) s = s.descending();
        else s = s.ascending();

        Pageable pageable = PageRequest.of(page, size, s);

        return service.list(q, pageable)
                .map(DoctorMapper::toResponse);
    }

    /* ============================
       GET BY ID
       ============================ */
    @GetMapping("/{id}")
    public DoctorResponse get(@PathVariable Long id) {
        return DoctorMapper.toResponse(service.get(id));
    }

    /* ============================
       CREATE
       ============================ */
    @PostMapping
    public ResponseEntity<DoctorResponse> create(
            @Valid @RequestBody DoctorRequest req
    ) {
        Doctor saved = service.create(
                DoctorMapper.toEntity(req, null) // userId later from auth
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DoctorMapper.toResponse(saved));
    }

    /* ============================
       UPDATE
       ============================ */
    @PutMapping("/{id}")
    public DoctorResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest req
    ) {
        Doctor updated = service.update(id, d ->
                DoctorMapper.update(d, req)
        );
        return DoctorMapper.toResponse(updated);
    }

    /* ============================
       DELETE
       ============================ */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
