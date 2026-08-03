package com.medical.demo.service.record;

import com.medical.demo.dto.request.RecordUploadRequest;
import com.medical.demo.dto.response.RecordResponse;
import com.medical.demo.model.MedicalRecord;
import com.medical.demo.model.User;

import java.util.List;

public interface RecordService {
    RecordResponse uploadRecord(RecordUploadRequest request, User currentUser);
    MedicalRecord getRecordById(Long id);
    RecordResponse getRecordDetails(Long id);
    List<RecordResponse> getPatientRecords(Long patientId, User currentUser);
    List<RecordResponse> getDoctorRecords(Long doctorId);  // ✅ Changed to RecordResponse
    RecordResponse updateRecord(Long id, MedicalRecord recordDetails, User currentUser);
    void deleteRecord(Long id, User currentUser);
    RecordResponse verifyRecordOnBlockchain(Long id);
    byte[] downloadRecord(Long id, User currentUser);
    List<RecordResponse> getRecordAccessHistory(Long recordId);
}