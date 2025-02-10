package tn.esprit.eventsproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServicesImpl implements IEventServices {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final LogisticsRepository logisticsRepository;

    @Override
    public Participant addParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    @Override
    public Event addAffectEvenParticipant(Event event, int idParticipant) {
        Participant participant = participantRepository.findById(idParticipant).orElse(null);

        if (participant == null) {
            log.warn("Participant avec ID {} introuvable.", idParticipant);
            return event; // Retourner l'event tel quel si le participant n'existe pas
        }

        if (participant.getEvents() == null) {
            participant.setEvents(new HashSet<>());
        }
        participant.getEvents().add(event);

        if (event.getParticipants() == null) {
            event.setParticipants(new HashSet<>());
        }
        event.getParticipants().add(participant);

        participantRepository.save(participant);
        return eventRepository.save(event);
    }

    @Override
    public Event addAffectEvenParticipant(Event event) {
        if (event.getParticipants() == null) {
            log.warn("L'événement ne contient aucun participant.");
            return event;
        }

        for (Participant aParticipant : event.getParticipants()) {
            Participant participant = participantRepository.findById(aParticipant.getIdPart()).orElse(null);
            if (participant == null) {
                log.warn("Participant avec ID {} introuvable.", aParticipant.getIdPart());
                continue; // Passer au participant suivant
            }

            if (participant.getEvents() == null) {
                participant.setEvents(new HashSet<>());
            }
            participant.getEvents().add(event);

            participantRepository.save(participant);
        }
        return eventRepository.save(event);
    }

    @Override
    public Logistics addAffectLog(Logistics logistics, String descriptionEvent) {
        Event event = eventRepository.findByDescription(descriptionEvent);

        if (event == null) {
            log.warn("Événement avec description '{}' introuvable.", descriptionEvent);
            return null;
        }

        if (event.getLogistics() == null) {
            event.setLogistics(new HashSet<>());
        }
        event.getLogistics().add(logistics);

        eventRepository.save(event);
        return logisticsRepository.save(logistics);
    }

    @Override
    public List<Logistics> getLogisticsDates(LocalDate date_debut, LocalDate date_fin) {
        List<Event> events = eventRepository.findByDateDebutBetween(date_debut, date_fin);
        List<Logistics> logisticsList = new ArrayList<>();

        for (Event event : events) {
            if (event.getLogistics() != null) {
                for (Logistics logistics : event.getLogistics()) {
                    if (logistics.isReserve()) {
                        logisticsList.add(logistics);
                    }
                }
            }
        }
        return logisticsList;
    }

    @Scheduled(cron = "0 0/1 * * * *") // Correction du cron pour exécuter toutes les minutes
    @Override
    public void calculCout() {
        List<Event> events = eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache(
                "Tounsi", "Ahmed", Tache.ORGANISATEUR);

        for (Event event : events) {
            log.info("Calcul du coût pour l'événement: {}", event.getDescription());

            float somme = 0f; // Réinitialisation pour chaque événement

            if (event.getLogistics() != null) {
                for (Logistics logistics : event.getLogistics()) {
                    if (logistics.isReserve()) {
                        somme += logistics.getPrixUnit() * logistics.getQuantite();
                    }
                }
            }
            event.setCout(somme);
            eventRepository.save(event);
            log.info("Coût de l'événement {} : {}", event.getDescription(), somme);
        }
    }

    @Override
    public List<Participant> getParReservLogis() {
        return participantRepository.participReservLogis(true, Tache.ORGANISATEUR);
    }
}
