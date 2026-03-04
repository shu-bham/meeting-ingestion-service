package com.soulside.service;

import com.soulside.dto.MeetingEventRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeetingEventHandlerFactory {

    private final Map<String, MeetingEventHandler<?>> handlers;

    public MeetingEventHandlerFactory(List<MeetingEventHandler<?>> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        MeetingEventHandler::supportedEvent,
                        handler -> handler
                ));
    }

    @SuppressWarnings("unchecked")
    public <T extends MeetingEventRequest> MeetingEventHandler<T> getHandler(String event) {
        MeetingEventHandler<?> handler = handlers.get(event);
        if (handler == null) {
            throw new IllegalArgumentException("No handler found for event: " + event);
        }
        return (MeetingEventHandler<T>) handler;
    }
}