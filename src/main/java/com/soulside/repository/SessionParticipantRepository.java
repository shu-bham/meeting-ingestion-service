package com.soulside.repository;

import com.soulside.model.MeetingSession;
import com.soulside.model.SessionParticipant;
import com.soulside.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {
    Optional<SessionParticipant> findBySessionAndUser(MeetingSession session, User user);
}
