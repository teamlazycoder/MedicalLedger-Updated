
package com.medical.demo.mapper;

import com.medical.demo.dto.response.ConsentResponse;
import com.medical.demo.model.ConsentPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConsentMapper {

    ConsentMapper INSTANCE = Mappers.getMapper(ConsentMapper.class);

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.firstName", target = "patientName")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.firstName", target = "doctorName")
    ConsentResponse toResponse(ConsentPolicy consent);
}