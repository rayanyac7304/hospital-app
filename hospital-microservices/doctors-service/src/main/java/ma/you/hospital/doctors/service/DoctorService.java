package ma.you.hospital.doctors.service;

import ma.you.hospital.doctors.domain.Doctor;
import ma.you.hospital.doctors.repositories.DoctorRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository repo;

    public DoctorService(DoctorRepository repo) {
        this.repo = repo;
    }

    /* ============================
       LIST + SEARCH
       ============================ */
    public Page<Doctor> list(String q, Pageable pageable) {
        return repo.search(q, pageable);
    }

    /* ============================
       GET
       ============================ */
    public Doctor get(Long id) {
        return repo.findById(id).orElse(null);
    }

    /* ============================
       CREATE
       ============================ */
    public Doctor create(Doctor d) {
        return repo.save(d);
    }

    /* ============================
       UPDATE
       ============================ */
    public Doctor update(Long id, Consumer<Doctor> updater) {
        Doctor d = get(id);
        if (d == null) return null;

        updater.accept(d);
        return repo.save(d);
    }

    /* ============================
       DELETE
       ============================ */
    public void delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        }
    }

    /* ============================
       DELETE BY USER ID
       ============================ */
}
