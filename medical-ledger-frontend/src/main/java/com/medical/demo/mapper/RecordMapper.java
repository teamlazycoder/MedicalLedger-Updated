package com.medical.demo.mapper;

import com.medical.demo.dto.response.RecordResponse;
import com.medical.demo.model.MedicalRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RecordMapper {

    RecordMapper INSTANCE = Mappers.getMapper(RecordMapper.class);

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.firstName", target = "patientName")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.firstName", target = "doctorName")
    RecordResponse toResponse(MedicalRecord record);
}
