package com.medical.demo.mapper;

import com.medical.demo.dto.request.RegisterRequest;
import com.medical.demo.dto.response.DoctorResponse;
import com.medical.demo.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "consentPolicies", ignore = true)
    @Mapping(target = "isAvailable", constant = "true")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Doctor toEntity(RegisterRequest request);

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.id", target = "userId")
    DoctorResponse toResponse(Doctor doctor);
}
