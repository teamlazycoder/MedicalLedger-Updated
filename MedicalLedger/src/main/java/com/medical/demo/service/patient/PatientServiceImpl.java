package com.medical.demo.service.patient;

import com.medical.demo.dto.response.PatientResponse;
import com.medical.demo.exception.ResourceNotFoundException;
import com.medical.demo.model.Patient;
import com.medical.demo.repository.PatientRepository;
import com.medical.demo.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    // REMOVED @Cacheable - this was causing Redis errors
    public PatientResponse getPatientDetails(Long id) {
        Patient patient = getPatientById(id);
        return mapToPatientResponse(patient);
    }

    @Override
    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user id: " + userId));
    }

    @Override
    @Transactional
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = getPatientById(id);
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setGender(patientDetails.getGender());
        patient.setBloodType(patientDetails.getBloodType());
        patient.setContactNumber(patientDetails.getContactNumber());
        patient.setAddress(patientDetails.getAddress());
        patient.setAllergies(patientDetails.getAllergies());
        patient.setChronicConditions(patientDetails.getChronicConditions());
        return patientRepository.save(patient);
    }

    @Override
    public List<PatientResponse> searchPatients(String searchTerm) {
        return patientRepository.searchPatients(searchTerm)
                .stream()
                .map(this::mapToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse getPatientProfile(Long patientId) {
        return getPatientDetails(patientId);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patient.getUser().setIsActive(false);
        patientRepository.delete(patient);
    }

    private PatientResponse mapToPatientResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setUserId(patient.getUser().getId());
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());
        response.setEmail(patient.getUser().getEmail());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setGender(patient.getGender());
        response.setBloodType(patient.getBloodType());
        response.setContactNumber(patient.getContactNumber());
        response.setAddress(patient.getAddress());
        response.setAllergies(patient.getAllergies());
        response.setChronicConditions(patient.getChronicConditions());
        response.setTotalRecords(medicalRecordRepository.countByPatientId(patient.getId()));
        response.setCreatedAt(patient.getCreatedAt());
        response.setUpdatedAt(patient.getUpdatedAt());
        return response;
    }
}