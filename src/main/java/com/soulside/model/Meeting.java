package com.soulside.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meeting")
public class Meeting extends BaseEntity {

    @Column(name = "meeting_id", unique = true, nullable = false)
    private String meetingId;

    private String title;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "meeting_created_at")
    private Instant meetingCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_organizer_id")
    private User organizer;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingSession> sessions = new ArrayList<>();

    public Meeting() {
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Instant getMeetingCreatedAt() {
        return meetingCreatedAt;
    }

    public void setMeetingCreatedAt(Instant meetingCreatedAt) {
        this.meetingCreatedAt = meetingCreatedAt;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public List<MeetingSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<MeetingSession> sessions) {
        this.sessions = sessions;
    }
}
