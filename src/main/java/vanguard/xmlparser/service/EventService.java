package vanguard.xmlparser.service;

import vanguard.xmlparser.repository.modal.Event;

import java.util.List;

public interface EventService {
    List<Event> readAndSaveEvents(String filePath);
    List<Event> filterEvents();
}
