package ma.you.hospital.medical_record.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "medical_record")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // External reference (patient microservice)
    @Column(nullable = false)
    private Long patientId;

    private String title;


    // Lifecycle
    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean archived;

    private LocalDate archivedAt;

    /**
     * Logical reference to the container holding documents
     * (folder, bucket prefix, archive unit).
     */
   /* @Column(nullable = false, length = 500)
    private String documentContainerRef;

    @PrePersist
    void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDateTime.now();
        archived = false;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}*/
}