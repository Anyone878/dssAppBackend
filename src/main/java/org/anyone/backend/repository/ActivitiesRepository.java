package org.anyone.backend.repository;

import org.anyone.backend.model.Activities;
import org.anyone.backend.model.Pet;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ActivitiesRepository extends CrudRepository<Activities, Integer> {
    Iterable<Activities> findAllByPet(Pet pet);

    Iterable<Activities> findAllByPetAndDateBetween(Pet pet, LocalDate start, LocalDate end);

    Iterable<Activities> findAllByPetAndDateAfter(Pet pet, LocalDate start);
}
