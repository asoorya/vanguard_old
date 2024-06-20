package vanguard.xmlparser.controller;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vanguard.xmlparser.repository.modal.Event;
import vanguard.xmlparser.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Validated
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/read")
    public ResponseEntity<List<Event>> readEvents(@RequestParam @NotEmpty String filePath) {
        List<Event> events = eventService.readAndSaveEvents(filePath);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Event>> filterEvents() {
        List<Event> events = eventService.filterEvents();
        return ResponseEntity.ok(events);
    }
}

