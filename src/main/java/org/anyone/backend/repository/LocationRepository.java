package org.anyone.backend.repository;

import org.anyone.backend.model.Location;
import org.anyone.backend.model.Pet;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface LocationRepository extends CrudRepository<Location, Integer> {
    Iterable<Location> findAllByPet(Pet pet);
    Iterable<Location> findAllByPetAndRecordDateTimeBetweenOrderByRecordDateTime(Pet pet, LocalDateTime start, LocalDateTime end);
    Iterable<Location> findAllByPetAndRecordDateTimeAfterOrderByRecordDateTime(Pet pet, LocalDateTime Start);
    Iterable<Location> findByPetOrderByRecordDateTimeDesc(Pet pet);
}
