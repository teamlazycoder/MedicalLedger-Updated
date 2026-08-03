package com.medical.demo.mapper;

import com.medical.demo.dto.request.RegisterRequest;
import com.medical.demo.dto.response.PatientResponse;
import com.medical.demo.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "consentPolicies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Patient toEntity(RegisterRequest request);

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.id", target = "userId")
    PatientResponse toResponse(Patient patient);
}
