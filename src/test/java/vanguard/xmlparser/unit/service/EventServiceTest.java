package vanguard.xmlparser.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vanguard.xmlparser.repository.EventRepository;
import vanguard.xmlparser.repository.modal.Event;
import vanguard.xmlparser.service.EventServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadAndSaveEvents() {
        List<Event> mockEvents = List.of(new Event());
        when(eventRepository.saveAll(anyList())).thenReturn(mockEvents);

        List<Event> events = eventService.readAndSaveEvents("src/test/resources/events/sample-event.xml");

        assertNotNull(events);
        verify(eventRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testReadAndSaveEvents_InvalidFilePath() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            eventService.readAndSaveEvents("invalid/path/sample-event.xml");
        });

        String expectedMessage = "Error parsing XML file";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(eventRepository, times(0)).saveAll(anyList());
    }

    @Test
    void testFilterEvents() {
        Event event1 = new Event();
        event1.setSellerParty("EMU_BANK");
        event1.setPremiumCurrency("AUD");
        event1.setBuyerParty("BUYER1");

        Event event2 = new Event();
        event2.setSellerParty("BISON_BANK");
        event2.setPremiumCurrency("USD");
        event2.setBuyerParty("BUYER2");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<Event> filteredEvents = eventService.filterEvents();

        assertEquals(2, filteredEvents.size());
    }

    @Test
    void testFilterEvents_NoMatch() {
        Event event1 = new Event();
        event1.setSellerParty("EMU_BANK");
        event1.setPremiumCurrency("USD");
        event1.setBuyerParty("BUYER1");

        Event event2 = new Event();
        event2.setSellerParty("BISON_BANK");
        event2.setPremiumCurrency("AUD");
        event2.setBuyerParty("BUYER2");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<Event> filteredEvents = eventService.filterEvents();

        assertTrue(filteredEvents.isEmpty());
    }

    @Test
    void testFilterEvents_Anagram() {
        Event event1 = new Event();
        event1.setSellerParty("EMU_BANK");
        event1.setPremiumCurrency("AUD");
        event1.setBuyerParty("KEMU_BAN");

        when(eventRepository.findAll()).thenReturn(List.of(event1));

        List<Event> filteredEvents = eventService.filterEvents();

        assertTrue(filteredEvents.isEmpty());
    }

    @Test
    void testFilterEvents_EmptyDatabase() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        List<Event> filteredEvents = eventService.filterEvents();

        assertTrue(filteredEvents.isEmpty());
    }
}



