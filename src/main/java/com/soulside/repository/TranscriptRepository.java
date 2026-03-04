package com.soulside.repository;

import com.soulside.model.MeetingSession;
import com.soulside.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long> {

    List<Transcript> findBySession(MeetingSession session);
}
