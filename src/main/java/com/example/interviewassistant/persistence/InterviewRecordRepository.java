package com.example.interviewassistant.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRecordRepository extends JpaRepository<InterviewRecord, Long> {
}
