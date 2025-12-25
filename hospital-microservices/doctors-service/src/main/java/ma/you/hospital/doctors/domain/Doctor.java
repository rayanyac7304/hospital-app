package ma.you.hospital.doctors.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String firstName;

    @Column(nullable=false) private String lastName;

    @Column(nullable=false) private String specialty;


    private Long userId;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist void prePersist(){ createdAt = updatedAt = Instant.now(); }
    @PreUpdate  void preUpdate(){  updatedAt = Instant.now(); }


}
