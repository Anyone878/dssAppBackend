package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.anyone.backend.model.Feeder;
import org.anyone.backend.model.FeedingSchedules;
import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.FeederRepository;
import org.anyone.backend.repository.FeedingSchedulesRepository;
import org.anyone.backend.repository.PetRepository;
import org.anyone.backend.repository.UserRepository;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feeding_schedules")
public class FeedingSchedulesController {
    private final UserRepository userRepository;
    private final FeedingSchedulesRepository feedingSchedulesRepository;
    private final PetRepository petRepository;
    private final FeederRepository feederRepository;
    private final Logger logger = LoggerFactory.getLogger(FeedingSchedules.class);


    public FeedingSchedulesController(UserRepository userRepository, FeedingSchedulesRepository feedingSchedulesRepository, PetRepository petRepository, FeederRepository feederRepository) {
        this.userRepository = userRepository;
        this.feedingSchedulesRepository = feedingSchedulesRepository;
        this.petRepository = petRepository;
        this.feederRepository = feederRepository;
    }

    @GetMapping
    ResponseData<?> getFeedingSchedules(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails) {
        Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
        if (optionalUsers.isEmpty()) {
            return ResponseData.userNotFoundResponse();
        }
        Users users = optionalUsers.get();

        Iterable<FeedingSchedules> feedingSchedulesIterable = feedingSchedulesRepository.findAllByUser(users);
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
    ResponseData<?> addFeedingSchedules(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode jsonNode
    ) {
        // getting user data
        Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
        if (optionalUsers.isEmpty()) {
            return new ResponseData<>(401, "user not found");
        }
        Users user = optionalUsers.get();
        // getting pet data
        Optional<Pet> optionalPet = petRepository.findByUser(user);
        if (optionalPet.isEmpty()) {
            return new ResponseData<>(404, "pet not found");
        }
        Pet pet = optionalPet.get();
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
        Iterator<JsonNode> feedingSchedulesArrayNodes = jsonNode.withArray("feedingSchedules").elements();
        while (feedingSchedulesArrayNodes.hasNext()) {
            JsonNode jsonNode1 = feedingSchedulesArrayNodes.next();
            if (!jsonNode1.isTextual()) {
                logger.error("node is not a text");
                return ResponseData.badRequestBodyResponse();
            }
            try {
                LocalDateTime feedingDateTime = LocalDateTime.parse(jsonNode1.asText());
                LocalTime feedingTime = feedingDateTime.toLocalTime();
                FeedingSchedules feedingSchedules = new FeedingSchedules();
                feedingSchedules.setFeedingTime(feedingTime);
                feedingSchedules.setFeeder(feeder);
                feedingSchedules.setPet(pet);
                feedingSchedules.setUser(user);
                feedingSchedulesRepository.save(feedingSchedules);
            } catch (DateTimeParseException e) {
                logger.error(e.getParsedString());
                return ResponseData.badRequestBodyResponse();
            } catch (Exception e) {
                logger.error(e.toString());
                return ResponseData.serverFailureResponse();
            }
        }
        return new ResponseData<>(200, "feeding schedules set");
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
            return new ResponseData<>(200, "entity deleted", feedingSchedules);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }
}
