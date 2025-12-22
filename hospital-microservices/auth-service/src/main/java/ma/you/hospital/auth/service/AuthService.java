package ma.you.hospital.auth.service;

import ma.you.hospital.auth.client.PatientServiceClient;
import ma.you.hospital.auth.domain.Role;
import ma.you.hospital.auth.domain.User;
import ma.you.hospital.auth.dto.AuthResponse;
import ma.you.hospital.auth.dto.LoginRequest;
import ma.you.hospital.auth.dto.RegisterRequest;
import ma.you.hospital.auth.repository.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PatientServiceClient patientServiceClient;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       PatientServiceClient patientServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.patientServiceClient = patientServiceClient;
    }


    public AuthResponse register(RegisterRequest request) {

        Role role = Role.valueOf(request.getRole().toUpperCase());

        if (role == Role.PATIENT) {
            if (request.getFirstName() == null ||
                    request.getLastName() == null ||
                    request.getGender() == null ||
                    request.getBirthDate() == null) {
                throw new IllegalArgumentException("Patient data is required");
            }
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        if (role == Role.PATIENT) {
            createPatientProfile(user.getId(), request);
        }

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private void createPatientProfile(Long userId, RegisterRequest request) {
        try {
            Map<String, Object> patientRequest = new HashMap<>();
            patientRequest.put("userId", userId);
            patientRequest.put("firstName", request.getFirstName());
            patientRequest.put("lastName", request.getLastName());
            patientRequest.put("gender", request.getGender());
            patientRequest.put("birthDate", request.getBirthDate());
            patientRequest.put("phone", request.getPhone());
            patientRequest.put("address", request.getAddress());

            patientServiceClient.createPatientFromUser(patientRequest);
            System.out.println("✅ Patient profile created successfully for userId: " + userId);
        } catch (Exception e) {
            System.err.println("❌ Failed to create patient profile: " + e.getMessage());
            e.printStackTrace();
            // Don't fail user creation if patient creation fails
        }
    }


    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid credentials");
        }

        User user = userRepository
                .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole().name());
                    userMap.put("enabled", user.getEnabled());
                    return userMap;
                })
                .toList();
    }

    public Map<String, Object> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("role", user.getRole().name());
        userMap.put("enabled", user.getEnabled());
        return userMap;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Impossible de supprimer un compte administrateur");
        }


        if (user.getRole() == Role.PATIENT) {
            deletePatientProfile(id);
        }

        userRepository.deleteById(id);
    }

    private void deletePatientProfile(Long userId) {
        try {
            patientServiceClient.deletePatientByUserId(userId);
            System.out.println("✅ Patient profile deleted for userId: " + userId);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to delete patient profile: " + e.getMessage());
            // Continue with user deletion even if patient deletion fails
        }
    }

    public void updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Prevent changing admin accounts
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Impossible de modifier un compte administrateur");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        userRepository.save(user);
    }


}