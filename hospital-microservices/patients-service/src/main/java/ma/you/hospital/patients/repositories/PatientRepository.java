package ma.you.hospital.patients.repositories;

import ma.you.hospital.patients.domain.Patient;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p FROM Patient p " +
            "WHERE (:q IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Patient> search(String q, Pageable pageable);


}
