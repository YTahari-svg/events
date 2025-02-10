package tn.esprit.eventsproject.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAffectEvenParticipant() {
        // Création d'un participant et d'un événement pour le test
        Participant mockParticipant = new Participant();
        mockParticipant.setIdPart(1);

        Event mockEvent = new Event();
        mockEvent.setIdEvent(1);

        // Simulation du repository
        when(participantRepository.findById(1)).thenReturn(Optional.of(mockParticipant));
        when(eventRepository.save(mockEvent)).thenReturn(mockEvent);

        // Ajout du participant à l'événement
        Event updatedEvent = eventServices.addAffectEvenParticipant(mockEvent, 1);

        // Vérifications
        assertNotNull(updatedEvent); // L'événement ne doit pas être null après l'ajout
        verify(participantRepository, times(1)).findById(1); // Vérification que findById a été appelé
        verify(eventRepository, times(1)).save(mockEvent); // Vérification que save a été appelé
    }
}