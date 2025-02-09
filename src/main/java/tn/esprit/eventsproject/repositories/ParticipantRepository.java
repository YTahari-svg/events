package tn.esprit.eventsproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    @Query("SELECT DISTINCT p from Participant p join p.events evnts join evnts.logistics logis where logis.reserve=?1 AND p.tache=?2")
    List<Participant> participReservLogis (Boolean state, Tache tache);
}