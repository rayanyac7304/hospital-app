package ma.you.hospital.appointments.repositories;

import ma.you.hospital.appointments.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find appointments by patient
    List<Appointment> findByPatientId(Long patientId);

    // Find appointments by doctor
    List<Appointment> findByDoctorId(Long doctorId);

    // Find appointments by date
    List<Appointment> findByDate(LocalDate date);

    // Find appointments by status
    List<Appointment> findByStatus(String status);

    // Check if time slot is available for a doctor on a specific date
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctorId = :doctorId " +
            "AND a.date = :date AND a.time = :time AND a.status != 'CANCELLED'")
    boolean existsByDoctorAndDateTime(@Param("doctorId") Long doctorId,
                                      @Param("date") LocalDate date,
                                      @Param("time") LocalTime time);
}