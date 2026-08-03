package com.medical.demo.service.doctor;

import com.medical.demo.dto.response.DoctorResponse;
import com.medical.demo.model.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor getDoctorById(Long id);
    DoctorResponse getDoctorDetails(Long id);
    Doctor getDoctorByUserId(Long userId);
    Doctor updateDoctor(Long id, Doctor doctorDetails);
    List<DoctorResponse> searchDoctors(String searchTerm);
    List<DoctorResponse> getDoctorsBySpecialty(String specialty);
    DoctorResponse getDoctorProfile(Long doctorId);
}