package com.example.interviewassistant.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InterviewRecordService {

    private static final Logger log = LoggerFactory.getLogger(InterviewRecordService.class);

    private final InterviewRecordRepository repository;
    private final ObjectMapper objectMapper;

    public InterviewRecordService(InterviewRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void save(InterviewActionType actionType, String provider, Object request, Object response) {
        InterviewRecord record = new InterviewRecord();
        record.setActionType(actionType);
        record.setProvider(provider == null ? "unknown" : provider);
        record.setRequestPayload(toJson(request));
        record.setResponsePayload(toJson(response));
        repository.save(record);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            log.warn("failed to serialize payload, fallback to toString()", ex);
            return value == null ? "null" : value.toString();
        }
    }
}
