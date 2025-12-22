package ma.you.hospital.doctors.repositories;

import ma.you.hospital.doctors.domain.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    @Query("""
        select d from Doctor d
        where
            (:q is null or :q = '' or
             lower(d.firstName) like lower(concat('%', :q, '%')) or
             lower(d.lastName) like lower(concat('%', :q, '%')) or
             lower(d.specialty) like lower(concat('%', :q, '%'))
            )
        """)
    Page<Doctor> search(@Param("q") String q, Pageable pageable);
}
