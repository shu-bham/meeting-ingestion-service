CREATE DATABASE IF NOT EXISTS meeting_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE meeting_db;

CREATE TABLE IF NOT EXISTS users (
    user_id    VARCHAR(255) NOT NULL,
    name       VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS meeting (
    meeting_id         VARCHAR(255) NOT NULL,
    title              VARCHAR(255),
    room_name          VARCHAR(255),
    status             VARCHAR(50),
    meeting_created_at TIMESTAMP NULL,
    started_at         TIMESTAMP NULL,
    ended_at           TIMESTAMP NULL,
    organizer_id       VARCHAR(255),
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (meeting_id),
    CONSTRAINT fk_meeting_organizer FOREIGN KEY (organizer_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS meeting_session (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    session_id VARCHAR(255) NOT NULL,
    meeting_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_id (session_id),
    CONSTRAINT fk_session_meeting FOREIGN KEY (meeting_id) REFERENCES meeting (meeting_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS session_participant (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    session_id BIGINT       NOT NULL,
    user_id    VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_user (session_id, user_id),
    CONSTRAINT fk_participant_session FOREIGN KEY (session_id) REFERENCES meeting_session (id),
    CONSTRAINT fk_participant_user    FOREIGN KEY (user_id)    REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS transcript (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    transcript_id   VARCHAR(255) NOT NULL,
    session_id      BIGINT       NOT NULL,
    speaker_id      VARCHAR(255) NOT NULL,
    sequence_number INT,
    content         TEXT,
    start_offset    INT,
    end_offset      INT,
    language        VARCHAR(50),
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_transcript_id (transcript_id),
    CONSTRAINT fk_transcript_session FOREIGN KEY (session_id) REFERENCES meeting_session (id),
    CONSTRAINT fk_transcript_speaker FOREIGN KEY (speaker_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
