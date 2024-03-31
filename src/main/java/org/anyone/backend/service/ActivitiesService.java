package org.anyone.backend.service;

import org.anyone.backend.model.Activities;
import org.anyone.backend.model.Pet;
import org.anyone.backend.repository.ActivitiesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivitiesService {
    private final ActivitiesRepository activitiesRepository;

    public ActivitiesService(ActivitiesRepository activitiesRepository) {
        this.activitiesRepository = activitiesRepository;
    }

    public Iterable<Activities> getActivities(Pet pet) {
        return activitiesRepository.findAllByPet(pet);
    }

    public Iterable<Activities> getActivities(Pet pet, LocalDate start) {
        return activitiesRepository.findAllByPetAndDateAfter(pet, start);
    }

    public Iterable<Activities> getActivities(Pet pet, LocalDate start, LocalDate end) {
        return activitiesRepository.findAllByPetAndDateBetween(pet, start, end);
    }

    public ArrayList<Activities> getActivitiesList(Pet pet) {
        return convertToList(getActivities(pet));
    }

    public ArrayList<Activities> getActivitiesList(Pet pet, LocalDate start) {
        return convertToList(getActivities(pet, start));
    }

    public ArrayList<Activities> getActivitiesList(Pet pet, LocalDate start, LocalDate end) {
        return convertToList(getActivities(pet, start, end));
    }

    ArrayList<Activities> convertToList(Iterable<Activities> activitiesIterable) {
        ArrayList<Activities> activities = new ArrayList<>();
        activitiesIterable.forEach(activities::add);
        return activities;
    }
}
