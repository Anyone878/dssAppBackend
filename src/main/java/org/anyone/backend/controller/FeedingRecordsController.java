package org.anyone.backend.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.FeedingRecords;
import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.FeedingRecordsRepository;
import org.anyone.backend.repository.PetRepository;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/feeding_records")
public class FeedingRecordsController {
    private final UserRepository userRepository;
    private final FeedingRecordsRepository feedingRecordsRepository;
    private final PetRepository petRepository;
    private final Logger logger = LoggerFactory.getLogger(FeedingRecordsController.class);

    public FeedingRecordsController(UserRepository userRepository, FeedingRecordsRepository feedingRecordsRepository, PetRepository petRepository) {
        this.userRepository = userRepository;
        this.feedingRecordsRepository = feedingRecordsRepository;
        this.petRepository = petRepository;
    }

    record GrabFeedingRecords(@JsonIgnore Pet pet, LocalDate feedingDate, float foodAmount, short feedingTimes) {
        @Override
        public String toString() {
            return "GrabFeedingRecords{" +
                    "pet=" + pet +
                    ", date=" + feedingDate +
                    ", amount=" + foodAmount +
                    ", feeding times=" + feedingTimes +
                    '}';
        }
    }

    @GetMapping(params = {"start"})
    ResponseData<?> getFeedingRecords(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end", required = false, defaultValue = "null") String end
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            LocalDateTime startDT = LocalDateTime.parse(start);
            LocalDateTime endDT = Objects.equals(end, "null") ? null : LocalDateTime.parse(end);
            Iterable<FeedingRecords> feedingRecordsIterable = endDT == null ?
                    feedingRecordsRepository.findAllByPetAndFeedingDateTimeAfterOrderByFeedingDateTimeDesc(pet, startDT) :
                    feedingRecordsRepository.findAllByPetAndFeedingDateTimeBetweenOrderByFeedingDateTimeDesc(pet, startDT, endDT);
            List<FeedingRecords> feedingRecords = new ArrayList<>();
            feedingRecordsIterable.forEach(feedingRecords::add);
            return new ResponseData<>(200, "feeding records found", feedingRecords);
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @GetMapping
    ResponseData<?> getLatestFeedingRecord(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "isLatest", required = false, defaultValue = "false") Boolean isLatest
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            if (isLatest) {
                Optional<FeedingRecords> optionalFeedingRecords = feedingRecordsRepository
                        .findFirstByPetOrderByFeedingDateTimeDesc(pet);
                if (optionalFeedingRecords.isEmpty()) {
                    return new ResponseData<>(404, "feeding record not found");
                } else {
                    return new ResponseData<>(200, "feeding record found", optionalFeedingRecords.get());
                }
            } else {
                Iterable<FeedingRecords> feedingRecordsIterable = feedingRecordsRepository.findAllByPet(pet);
                List<FeedingRecords> feedingRecords = new ArrayList<>();
                feedingRecordsIterable.forEach(feedingRecords::add);
                return new ResponseData<>(200, "feeding records found", feedingRecords);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    /**
     * NOTE: Time sensitive.
     */
    @GetMapping(path = {"/daily"})
    ResponseData<?> getDaily(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "start", required = false, defaultValue = "2000-01-01T01:00:00") String start,
            @RequestParam(name = "end", required = false, defaultValue = "null") String end
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            LocalDateTime startDT = LocalDateTime.parse(start);
            LocalDateTime endDT = Objects.equals(end, "null") ? null : LocalDateTime.parse(end);
            Iterable<FeedingRecords> feedingRecordsIterable = endDT == null ?
                    feedingRecordsRepository.findAllByPetAndFeedingDateTimeAfterOrderByFeedingDateTimeDesc(pet, startDT) :
                    feedingRecordsRepository.findAllByPetAndFeedingDateTimeBetweenOrderByFeedingDateTimeDesc(pet, startDT, endDT);
            HashMap<LocalDate, float[]> map = new HashMap<>();
            feedingRecordsIterable.forEach(feedingRecords -> {
                LocalDate k = feedingRecords.getFeedingDateTime().toLocalDate();
                if (!map.containsKey(k)) {
                    map.put(k, new float[]{feedingRecords.getFoodAmount(), 1.0F});
                } else {
                    float[] v = map.get(k);
                    v[0] += feedingRecords.getFoodAmount();
                    v[1] += 1;
                }
            });
            List<GrabFeedingRecords> grabFeedingRecords = new ArrayList<>();
            map.forEach((k, v) -> grabFeedingRecords.add(new GrabFeedingRecords(pet, k, v[0], (short) v[1])));
            return new ResponseData<>(200, "aggregated feeding records found", grabFeedingRecords);
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    private Users getUser(UserDetails userDetails) {
        Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
        return optionalUsers.orElse(null);
    }

    private Pet getPet(Users user) {
        Optional<Pet> optionalPet = petRepository.findByUser(user);
        return optionalPet.orElse(null);
    }
}
