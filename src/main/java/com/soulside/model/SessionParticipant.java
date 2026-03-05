package com.soulside.model;

import jakarta.persistence.*;

@Entity
@Table(name = "session_participant", uniqueConstraints = @UniqueConstraint(columnNames = {"fk_session_id", "fk_user_id"}))
public class SessionParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_session_id", nullable = false)
    private MeetingSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    public SessionParticipant() {
    }

    public MeetingSession getSession() {
        return session;
    }

    public void setSession(MeetingSession session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
