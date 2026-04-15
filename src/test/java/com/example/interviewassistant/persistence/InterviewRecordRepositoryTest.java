package com.example.interviewassistant.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class InterviewRecordRepositoryTest {

    @Autowired
    private InterviewRecordRepository repository;

    @Test
    void shouldPersistInterviewRecord() {
        InterviewRecord record = new InterviewRecord();
        record.setActionType(InterviewActionType.QUESTION_GENERATION);
        record.setProvider("rule-based");
        record.setRequestPayload("{\"role\":\"backend\"}");
        record.setResponsePayload("{\"questions\":[]}");

        InterviewRecord saved = repository.save(record);

        assertNotNull(saved.getId());
        assertEquals(1, repository.count());
        assertFalse(repository.findAll().isEmpty());
    }
}
