CREATE DATABASE IF NOT EXISTS meeting_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE meeting_db;

CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    VARCHAR(255) NOT NULL,
    name       VARCHAR(255),
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS meeting
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    meeting_id         VARCHAR(255) NOT NULL,
    title              VARCHAR(255),
    room_name          VARCHAR(255),
    meeting_created_at DATETIME     NOT NULL,
    fk_organizer_id    BIGINT,
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_meeting_id (meeting_id),
    CONSTRAINT fk_meeting_organizer FOREIGN KEY (fk_organizer_id) REFERENCES users (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS meeting_session
(
    id            BIGINT                                                NOT NULL AUTO_INCREMENT,
    session_id    VARCHAR(255)                                          NOT NULL,
    fk_meeting_id BIGINT                                                NOT NULL,
    status        ENUM ('STARTED', 'ENDED', 'CANCELLED', 'IN_PROGRESS') NOT NULL,
    started_at    DATETIME                                              NULL,
    ended_at      DATETIME                                              NULL,
    created_at    DATETIME                                              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME                                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_meeting_session_id (session_id, fk_meeting_id),
    CONSTRAINT fk_session_meeting FOREIGN KEY (fk_meeting_id) REFERENCES meeting (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS session_participant
(
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    fk_session_id BIGINT   NOT NULL,
    fk_user_id    BIGINT   NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_user (fk_session_id, fk_user_id),
    CONSTRAINT fk_participant_session FOREIGN KEY (fk_session_id) REFERENCES meeting_session (id),
    CONSTRAINT fk_participant_user FOREIGN KEY (fk_user_id) REFERENCES users (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS transcript
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    transcript_id   VARCHAR(255) NOT NULL,
    fk_session_id   BIGINT       NOT NULL,
    fk_speaker_id   BIGINT       NOT NULL,
    sequence_number INT,
    content         TEXT,
    start_offset    INT,
    end_offset      INT,
    language        VARCHAR(50),
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_transcript_id (transcript_id),
    CONSTRAINT fk_transcript_session FOREIGN KEY (fk_session_id) REFERENCES meeting_session (id),
    CONSTRAINT fk_transcript_speaker FOREIGN KEY (fk_speaker_id) REFERENCES users (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
