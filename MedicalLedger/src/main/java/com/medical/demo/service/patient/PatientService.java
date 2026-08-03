package com.medical.demo.service.patient;

import com.medical.demo.dto.response.PatientResponse;
import com.medical.demo.model.Patient;

import java.util.List;

public interface PatientService {
    Patient getPatientById(Long id);
    PatientResponse getPatientDetails(Long id);
    Patient getPatientByUserId(Long userId);
    Patient updatePatient(Long id, Patient patientDetails);
    List<PatientResponse> searchPatients(String searchTerm);
    PatientResponse getPatientProfile(Long patientId);
    void deletePatient(Long id);
}