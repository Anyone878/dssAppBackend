package org.anyone.backend.controller;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseData<?> getFeedingRecords(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode requestBody
    ) {
        try {
            Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
            if (optionalUsers.isEmpty()) {
                return ResponseData.userNotFoundResponse();
            }
            Users user = optionalUsers.get();

            Optional<Pet> optionalPet = petRepository.findByUser(user);
            if (optionalPet.isEmpty()) {
                return new ResponseData<>(404, "pet not found");
            }
            Pet pet = optionalPet.get();

            String startKey = "start";
            String endKey = "end";
            if (!requestBody.has(startKey)) {
                return ResponseData.badRequestBodyResponse();
            }
            LocalDateTime start = LocalDateTime.parse(requestBody.get(startKey).asText());

            Iterable<FeedingRecords> feedingRecordsIterable;
            if (!requestBody.has(endKey)) {
                // if it has "start" key but no "end". Use "After" method.
                feedingRecordsIterable = feedingRecordsRepository
                        .findAllByPetAndFeedingDateTimeAfterOrderByFeedingDateTimeDesc(pet, start);
            } else {
                // if it has "end", use "Between".
                LocalDateTime end = LocalDateTime.parse(requestBody.get(endKey).asText());
                feedingRecordsIterable = feedingRecordsRepository
                        .findAllByPetAndFeedingDateTimeBetweenOrderByFeedingDateTimeDesc(pet, start, end);
            }
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
            Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
            if (optionalUsers.isEmpty()) {
                return ResponseData.userNotFoundResponse();
            }
            Users user = optionalUsers.get();

            Optional<Pet> optionalPet = petRepository.findByUser(user);
            if (optionalPet.isEmpty()) {
                return new ResponseData<>(404, "pet not found");
            }
            Pet pet = optionalPet.get();

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
}
