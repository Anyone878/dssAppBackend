package org.anyone.backend.repository;

import org.anyone.backend.model.Pet;
import org.anyone.backend.model.WeightRecords;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WeightRecordsRepository extends CrudRepository<WeightRecords, Integer> {
    Iterable<WeightRecords> findAllByPetOrderByRecordDateTime(Pet pet);

    Optional<WeightRecords> findFirstByPetOrderByRecordDateTimeDesc(Pet pet);

    Iterable<WeightRecords> findAllByPetAndRecordDateTimeBetweenOrderByRecordDateTime(Pet pet, LocalDateTime start, LocalDateTime end);

    Iterable<WeightRecords> findAllByPetAndRecordDateTimeAfterOrderByRecordDateTime(Pet pet, LocalDateTime start);
}
