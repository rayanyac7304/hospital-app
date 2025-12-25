package ma.you.hospital.billing.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import ma.you.hospital.billing.domain.Bill;
import ma.you.hospital.billing.dto.AppointmentDTO;
import ma.you.hospital.billing.dto.DoctorDTO;
import ma.you.hospital.billing.dto.PatientDTO;
import ma.you.hospital.billing.clients.AppointmentRestClient;
import ma.you.hospital.billing.clients.DoctorRestClient;
import ma.you.hospital.billing.clients.PatientRestClient;
import ma.you.hospital.billing.repositories.BillRepository;
import ma.you.hospital.billing.web.dto.BillRequestDTO;
import ma.you.hospital.billing.web.dto.BillResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final DoctorRestClient doctorRestClient;
    private final PatientRestClient patientRestClient;
    private final AppointmentRestClient appointmentRestClient;

    // -----------------------
    // Création de facture
    // -----------------------
    public BillResponseDTO createBill(BillRequestDTO request) {

        // Récupérer le rendez-vous
        AppointmentDTO appointment = appointmentRestClient.getAppointmentById(request.getAppointmentId());

        // Récupérer patient + docteur
        DoctorDTO doctor = doctorRestClient.getDoctorById(appointment.getDoctorId());
        PatientDTO patient = patientRestClient.getPatientById(appointment.getPatientId());

        Bill bill = Bill.builder()
                .appointmentId(appointment.getId())
                .doctorId(appointment.getDoctorId())
                .patientId(appointment.getPatientId())
                .amount(request.getAmount())
                .billDate(LocalDate.now())
                .description(request.getDescription())
                .build();

        Bill saved = billRepository.save(bill);

        return BillResponseDTO.builder()
                .bill(saved)
                .doctor(doctor)
                .patient(patient)
                .build();
    }

    // -----------------------
    // Lecture : toutes les factures
    // -----------------------
    @CircuitBreaker(name = "billingService", fallbackMethod = "getAllBillsFallback")
    public List<BillResponseDTO> getAllBills(String q) {

        List<BillResponseDTO> bills = billRepository.findAll()
                .stream()
                .map(bill -> {
                    DoctorDTO doctor = doctorRestClient.getDoctorById(bill.getDoctorId());
                    PatientDTO patient = patientRestClient.getPatientById(bill.getPatientId());

                    return BillResponseDTO.builder()
                            .bill(bill)
                            .doctor(doctor)
                            .patient(patient)
                            .build();
                })
                .collect(Collectors.toList());

        // 🔍 SEARCH FILTER
        if (q != null && !q.isBlank()) {
            String query = q.toLowerCase();

            bills = bills.stream()
                    .filter(b ->
                            (b.getDoctor() != null &&
                                    (b.getDoctor().getFirstName() + " " + b.getDoctor().getLastName())
                                            .toLowerCase().contains(query))
                                    ||
                                    (b.getPatient() != null &&
                                            (b.getPatient().getFirstName() + " " + b.getPatient().getLastName())
                                                    .toLowerCase().contains(query))
                    )
                    .collect(Collectors.toList());
        }

        return bills;
    }


    /**
     * Fallback de getAllBills : si un microservice externe (patients/doctors) est down,
     * on renvoie quand même la liste des factures, mais SANS les détails patient/doctor.
     */
    private List<BillResponseDTO> getAllBillsFallback(Throwable ex) {
        return billRepository.findAll().stream()
                .map(bill -> BillResponseDTO.builder()
                        .bill(bill)
                        .doctor(null)   // infos indisponibles
                        .patient(null)  // infos indisponibles
                        .build())
                .collect(Collectors.toList());
    }

    // -----------------------
    // Lecture : facture par id
    // -----------------------
    @CircuitBreaker(name = "billingService", fallbackMethod = "getBillByIdFallback")
    public BillResponseDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id).orElseThrow();

        DoctorDTO doctor = doctorRestClient.getDoctorById(bill.getDoctorId());
        PatientDTO patient = patientRestClient.getPatientById(bill.getPatientId());

        return BillResponseDTO.builder()
                .bill(bill)
                .doctor(doctor)
                .patient(patient)
                .build();
    }

    /**
     * Fallback de getBillById : si patient-service ou doctors-service est KO,
     * on retourne la facture seule, sans les détails externes.
     */
    private BillResponseDTO getBillByIdFallback(Long id, Throwable ex) {
        Bill bill = billRepository.findById(id).orElseThrow();

        return BillResponseDTO.builder()
                .bill(bill)
                .doctor(null)
                .patient(null)
                .build();
    }
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }



    /*
    public void generatePdf(Long id) {

    }*/


}
