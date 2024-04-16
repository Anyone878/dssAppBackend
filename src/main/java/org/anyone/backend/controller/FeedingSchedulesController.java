package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.Feeder;
import org.anyone.backend.model.FeedingSchedules;
import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.FeederRepository;
import org.anyone.backend.repository.FeedingSchedulesRepository;
import org.anyone.backend.repository.PetRepository;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.service.FeedingSchedulesService;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feeding_schedules")
public class FeedingSchedulesController {
    private final UserRepository userRepository;
    private final FeedingSchedulesRepository feedingSchedulesRepository;
    private final PetRepository petRepository;
    private final FeederRepository feederRepository;
    private final FeedingSchedulesService feedingSchedulesService;
    private final Logger logger = LoggerFactory.getLogger(FeedingSchedules.class);


    public FeedingSchedulesController(UserRepository userRepository, FeedingSchedulesRepository feedingSchedulesRepository, PetRepository petRepository, FeederRepository feederRepository, FeedingSchedulesService feedingSchedulesService) {
        this.userRepository = userRepository;
        this.feedingSchedulesRepository = feedingSchedulesRepository;
        this.petRepository = petRepository;
        this.feederRepository = feederRepository;
        this.feedingSchedulesService = feedingSchedulesService;
    }

    @GetMapping
    ResponseData<?> getFeedingSchedules(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails) {
        Users user = getUser(userDetails);
        if (user == null) return ResponseData.userNotFoundResponse();

        Iterable<FeedingSchedules> feedingSchedulesIterable = feedingSchedulesRepository.findAllByUser(user);
        List<FeedingSchedules> feedingSchedulesList = new ArrayList<>();
        feedingSchedulesIterable.forEach(feedingSchedulesList::add);
        return new ResponseData<>(200, "feeding schedules found", feedingSchedulesList);
    }

    /**
     * Need "feedingID" to allocate the data you want to update.
     * Need the updated "feedingTime" (LocalDateTime in ISO-8601).
     */
    @PutMapping
    ResponseData<?> updateFeedingSchedules(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode requestBody
    ) {
        Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
        if (optionalUsers.isEmpty()) {
            return ResponseData.userNotFoundResponse();
        }
        Users user = optionalUsers.get();

        logger.info(requestBody.toString());
        if ((!requestBody.has("feedingID")) || (!requestBody.has("feedingTime"))) {
            logger.error("bed request body");
            return ResponseData.badRequestBodyResponse();
        }
        try {
            int feedingID = requestBody.get("feedingID").asInt();
            LocalDateTime feedingTime = LocalDateTime.parse(requestBody.get("feedingTime").asText());
            Optional<FeedingSchedules> feedingSchedulesOptional = feedingSchedulesRepository.findByFeedingID(feedingID);
            if (feedingSchedulesOptional.isEmpty()) {
                return new ResponseData<>(404, "feeding schedule not found");
            }
            FeedingSchedules feedingSchedules = feedingSchedulesOptional.get();
            feedingSchedules.setFeedingTime(feedingTime.toLocalTime());
            feedingSchedulesRepository.save(feedingSchedules);
            // update feeding amount
            feedingSchedulesService.updateAmount(user);
            return new ResponseData<>(200, "data updated", feedingSchedules);
        } catch (DateTimeParseException dateTimeParseException) {
            logger.error(dateTimeParseException.getParsedString());
            return new ResponseData<>(400, "bad request body, cannot parse \"feedingTime\"");
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    /**
     * Need "feedingSchedules", an array.
     * ["LocalDateTime in ISO-8601", ...]
     */
    @PostMapping
    ResponseData<?> deleteAndAddFeedingSchedules(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode jsonNode
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");
            // getting feeder data
            Optional<Feeder> optionalFeeder = feederRepository.findByUser(user);
            if (optionalFeeder.isEmpty()) {
                return new ResponseData<>(404, "feeder not found");
            }
            Feeder feeder = optionalFeeder.get();

            logger.info(jsonNode.toString());
            if (!jsonNode.has("feedingSchedules")) {
                logger.error(jsonNode.toString());
                return ResponseData.badRequestBodyResponse();
            }
            if (!jsonNode.get("feedingSchedules").isArray()) {
                logger.error(jsonNode.toString());
                return new ResponseData<>(400, "bad request body, 'feedingSchedules' is not an array");
            }
            // validate all schedule data.
            for (JsonNode node : jsonNode.get("feedingSchedules")) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(node.asText());
                } catch (DateTimeParseException e) {
                    logger.error(e.getParsedString());
                    return ResponseData.badRequestBodyResponse();
                }
            }
            // delete all schedules.
            feedingSchedulesRepository.deleteAllByUser(user);
            // processing...
            for (JsonNode node : jsonNode.get("feedingSchedules")) {
                LocalTime time = LocalDateTime.parse(node.asText()).toLocalTime();
                feedingSchedulesRepository.save(new FeedingSchedules(user, pet, feeder, time));
            }
            // update feeding amount
            feedingSchedulesService.updateAmount(user);
            return getFeedingSchedules(userDetails);
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    /**
     * need "feedingID"
     * return the deleted data
     */
    @DeleteMapping
    ResponseData<?> deleteFeedingSchedule(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode requestBody
    ) {
        Users user = getUser(userDetails);
        if (user == null) return ResponseData.userNotFoundResponse();
        if (!requestBody.has("feedingID")) {
            return ResponseData.badRequestBodyResponse();
        }
        if (!requestBody.get("feedingID").isInt()) {
            return ResponseData.badRequestBodyResponse();
        }
        int feedingID = requestBody.get("feedingID").asInt();
        Optional<FeedingSchedules> feedingSchedulesOptional = feedingSchedulesRepository.findByFeedingID(feedingID);
        if (feedingSchedulesOptional.isEmpty()) {
            return new ResponseData<>(404, "feeding schedule not found");
        }
        FeedingSchedules feedingSchedules = feedingSchedulesOptional.get();
        try {
            feedingSchedulesRepository.delete(feedingSchedules);
            // update feeding amount
            feedingSchedulesService.updateAmount(user);
            return new ResponseData<>(200, "entity deleted", feedingSchedules);
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
