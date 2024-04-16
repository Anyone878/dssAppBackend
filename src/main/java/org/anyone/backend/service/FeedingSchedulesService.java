package org.anyone.backend.service;

import org.anyone.backend.model.Feeder;
import org.anyone.backend.model.FeedingSchedules;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.FeederRepository;
import org.anyone.backend.repository.FeedingSchedulesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class FeedingSchedulesService {
    private final FeedingSchedulesRepository feedingSchedulesRepository;
    private final FeederRepository feederRepository;
    private final Logger logger = LoggerFactory.getLogger(FeedingSchedulesService.class);

    public FeedingSchedulesService(FeedingSchedulesRepository feedingSchedulesRepository, FeederRepository feederRepository) {
        this.feedingSchedulesRepository = feedingSchedulesRepository;
        this.feederRepository = feederRepository;
    }

    private Iterable<FeedingSchedules> getFeedingSchedulesIter(Users user) {
        return feedingSchedulesRepository.findAllByUser(user);
    }

    public ArrayList<FeedingSchedules> getFeedingSchedules(Users user) {
        Iterable<FeedingSchedules> feedingSchedules = getFeedingSchedulesIter(user);
        ArrayList<FeedingSchedules> feedingSchedulesList = new ArrayList<>();
        feedingSchedules.forEach(feedingSchedulesList::add);
        return feedingSchedulesList;
    }

    /**
     * call this AFTER other operations have finished.
     * return the amount for each schedule.
     * ERROR when returning -1.
     */
    public float updateAmount(Users user) {
        ArrayList<FeedingSchedules> feedingSchedules = getFeedingSchedules(user);
        Optional<Feeder> feeder = feederRepository.findByUser(user);
        if (feeder.isEmpty()) return -1;
        if (feedingSchedules.isEmpty()) return -1;
        float each = feeder.get().getEverydayFoodPlan() / feedingSchedules.toArray().length;
        for (FeedingSchedules feedingSchedule : feedingSchedules) {
            feedingSchedule.setFoodAmount(each);
            feedingSchedulesRepository.save(feedingSchedule);
        }
        return each;
    }
}
