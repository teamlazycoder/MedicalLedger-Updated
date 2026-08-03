package com.medical.demo.service.doctor;

import com.medical.demo.dto.response.DoctorResponse;
import com.medical.demo.exception.ResourceNotFoundException;
import com.medical.demo.model.Doctor;
import com.medical.demo.repository.ConsentPolicyRepository;
import com.medical.demo.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final ConsentPolicyRepository consentPolicyRepository;

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    @Override
    @Cacheable(value = "doctorDetails", key = "#id")
    public DoctorResponse getDoctorDetails(Long id) {
        Doctor doctor = getDoctorById(id);
        return mapToDoctorResponse(doctor);
    }

    @Override
    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for user id: " + userId));
    }

    @Override
    @Transactional
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = getDoctorById(id);

        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setSpecialty(doctorDetails.getSpecialty());
        doctor.setLicenseNumber(doctorDetails.getLicenseNumber());
        doctor.setHospitalAffiliation(doctorDetails.getHospitalAffiliation());
        doctor.setYearsOfExperience(doctorDetails.getYearsOfExperience());
        doctor.setQualifications(doctorDetails.getQualifications());
        doctor.setIsAvailable(doctorDetails.getIsAvailable());

        log.info("Updated doctor profile for id: {}", id);
        return doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorResponse> searchDoctors(String searchTerm) {
        return doctorRepository.searchDoctors(searchTerm)
                .stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorResponse> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty)
                .stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse getDoctorProfile(Long doctorId) {
        return getDoctorDetails(doctorId);
    }

    private DoctorResponse mapToDoctorResponse(Doctor doctor) {
        DoctorResponse response = new DoctorResponse();
        response.setId(doctor.getId());
        response.setUserId(doctor.getUser().getId());
        response.setFirstName(doctor.getFirstName());
        response.setLastName(doctor.getLastName());
        response.setEmail(doctor.getUser().getEmail());
        response.setSpecialty(doctor.getSpecialty());
        response.setLicenseNumber(doctor.getLicenseNumber());
        response.setHospitalAffiliation(doctor.getHospitalAffiliation());
        response.setYearsOfExperience(doctor.getYearsOfExperience());
        response.setQualifications(doctor.getQualifications());
        response.setIsAvailable(doctor.getIsAvailable());
        response.setIsVerified(doctor.getIsVerified());
        response.setTotalPatients((long) consentPolicyRepository.findByDoctorId(doctor.getId()).size());
        response.setCreatedAt(doctor.getCreatedAt());
        response.setUpdatedAt(doctor.getUpdatedAt());
        return response;
    }
}