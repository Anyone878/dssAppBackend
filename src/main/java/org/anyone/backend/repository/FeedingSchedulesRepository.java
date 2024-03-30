package org.anyone.backend.repository;

import org.anyone.backend.model.FeedingSchedules;
import org.anyone.backend.model.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FeedingSchedulesRepository extends CrudRepository<FeedingSchedules, Integer> {
    Iterable<FeedingSchedules> findAllByUser(Users user);

    Optional<FeedingSchedules> findByFeedingID(int feedingID);

    @Transactional
    void deleteAllByUser(Users user);
}
