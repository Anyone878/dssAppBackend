package org.anyone.backend.repository;

import org.anyone.backend.model.FeedingRecords;
import org.anyone.backend.model.Pet;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FeedingRecordsRepository extends CrudRepository<FeedingRecords, Integer> {
    Iterable<FeedingRecords> findAllByPet(Pet pet);

    Iterable<FeedingRecords> findAllByPetAndFeedingDateTimeBetweenOrderByFeedingDateTimeDesc(Pet pet, LocalDateTime start, LocalDateTime end);

    Iterable<FeedingRecords> findAllByPetAndFeedingDateTimeAfterOrderByFeedingDateTimeDesc(Pet pet, LocalDateTime start);

    Optional<FeedingRecords> findFirstByPetOrderByFeedingDateTimeDesc(Pet pet);
}
