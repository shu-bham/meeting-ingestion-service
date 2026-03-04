package com.soulside.repository;

import com.soulside.model.Meeting;
import com.soulside.model.MeetingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingSessionRepository extends JpaRepository<MeetingSession, Long> {

    Optional<MeetingSession> findBySessionIdAndMeeting(String sessionId, Meeting meeting);
}
