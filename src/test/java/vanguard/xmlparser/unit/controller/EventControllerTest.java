package vanguard.xmlparser.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vanguard.xmlparser.controller.EventController;
import vanguard.xmlparser.controller.GlobalExceptionHandler;
import vanguard.xmlparser.repository.modal.Event;
import vanguard.xmlparser.service.EventService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testReadEvents() throws Exception {
        when(eventService.readAndSaveEvents("src/test/resources/events/sample-event.xml")).thenReturn(List.of(new Event()));

        mockMvc.perform(post("/api/events/read")
                        .param("filePath", "src/test/resources/events/sample-event.xml"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testReadEvents_InvalidFilePath() throws Exception {
        when(eventService.readAndSaveEvents("invalid/path/sample-event.xml")).thenThrow(new RuntimeException("Error parsing XML file"));

        mockMvc.perform(post("/api/events/read")
                        .param("filePath", "invalid/path/sample-event.xml"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error parsing XML file"));
    }

    @Test
    void testFilterEvents() throws Exception {
        when(eventService.filterEvents()).thenReturn(List.of(new Event()));

        mockMvc.perform(get("/api/events/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testFilterEvents_NoMatch() throws Exception {
        when(eventService.filterEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/events/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}



