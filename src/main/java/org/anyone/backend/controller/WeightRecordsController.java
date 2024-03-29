package org.anyone.backend.controller;

import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.model.WeightRecords;
import org.anyone.backend.repository.PetRepository;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.repository.WeightRecordsRepository;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("weight_records")
public class WeightRecordsController {
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final WeightRecordsRepository weightRecordsRepository;
    private final Logger logger = LoggerFactory.getLogger(WeightRecordsController.class);

    public WeightRecordsController(UserRepository userRepository, PetRepository petRepository, WeightRecordsRepository weightRecordsRepository) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.weightRecordsRepository = weightRecordsRepository;
    }

    @GetMapping
    ResponseData<?> getAll(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            Iterable<WeightRecords> weightRecordsIterable = weightRecordsRepository.findAllByPetOrderByRecordDateTime(pet);
            List<WeightRecords> weightRecords = new ArrayList<>();
            weightRecordsIterable.forEach(weightRecords::add);
            return new ResponseData<>(200, "weight records found", weightRecords);
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @GetMapping(params = {"isLatest"})
    ResponseData<?> getLatest(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "isLatest", required = false, defaultValue = "false") Boolean isLatest
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            if (isLatest) {
                Optional<WeightRecords> optionalWeightRecords = weightRecordsRepository.findFirstByPetOrderByRecordDateTimeDesc(pet);
                if (optionalWeightRecords.isEmpty()) return new ResponseData<>(404, "weight record not found");
                return new ResponseData<>(200, "weight records found", optionalWeightRecords.get());
            } else {
                return getAll(userDetails);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        }
    }

    @GetMapping(params = {"start", "end"})
    ResponseData<?> getBetween(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            LocalDateTime startDT = LocalDateTime.parse(start);
            LocalDateTime endDT = LocalDateTime.parse(end);
            Iterable<WeightRecords> weightRecordsIterable = weightRecordsRepository.findAllByPetAndRecordDateTimeBetweenOrderByRecordDateTime(pet, startDT, endDT);
            List<WeightRecords> weightRecords = new ArrayList<>();
            weightRecordsIterable.forEach(weightRecords::add);
            return new ResponseData<>(200, "weight records found", weightRecords);
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            return ResponseData.serverFailureResponse();
        }
    }

    @GetMapping(params = {"start"})
    ResponseData<?> getAfter(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "start") String start
    ) {
        try {
            Users user = getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            LocalDateTime startDT = LocalDateTime.parse(start);
            Iterable<WeightRecords> weightRecordsIterable = weightRecordsRepository.findAllByPetAndRecordDateTimeAfterOrderByRecordDateTime(pet, startDT);
            List<WeightRecords> weightRecords = new ArrayList<>();
            weightRecordsIterable.forEach(weightRecords::add);
            return new ResponseData<>(200, "weight records found", weightRecords);
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
