package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.Location;
import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.LocationRepository;
import org.anyone.backend.repository.PetRepository;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationController {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PetRepository petRepository;
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    public LocationController(UserRepository userRepository, LocationRepository locationRepository, PetRepository petRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.petRepository = petRepository;
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData<?> getLocations(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody JsonNode jsonNode
    ) {
        String username = userDetails.getUsername();
        Optional<Users> optionalUsers = userRepository.findUserByUsername(username);
        if (optionalUsers.isEmpty()) {
            return new ResponseData<>(401, "user not found", null);
        }
        Users user = optionalUsers.get();

        Optional<Pet> optionalPet = petRepository.findByUser(user);
        if (optionalPet.isEmpty()) {
            return new ResponseData<>(404, "pet not found", null);
        }
        Pet pet = optionalPet.get();

        // parse in ISO 8601, so check data validation first
        JsonNode startNode = jsonNode.get("start");
        JsonNode endNode = jsonNode.get("end");
        if (startNode == null) {
            return new ResponseData<>(400, "bad request body", null);
        }

        String start = startNode.asText();
        if (start.isEmpty()) {
            return new ResponseData<>(400, "bed request body", null);
        }
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(start);
            if (endNode == null || endNode.asText().isEmpty()) {
                Iterable<Location> locationIterable =
                        locationRepository.findAllByPetAndRecordDateTimeAfterOrderByRecordDateTime(pet, startDateTime);
                List<Location> locations = new ArrayList<>();
                locationIterable.forEach(locations::add);
                return new ResponseData<>(200, "locations found", locations);
            } else {
                String end = endNode.asText();
                LocalDateTime endDateTime = LocalDateTime.parse(end);
                Iterable<Location> locationIterable =
                        locationRepository.findAllByPetAndRecordDateTimeBetweenOrderByRecordDateTime(pet, startDateTime, endDateTime);
                List<Location> locations = new ArrayList<>();
                locationIterable.forEach(locations::add);
                return new ResponseData<>(200, "locations found", locations);
            }
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return new ResponseData<>(400, "bad datetime format, must be in ISO 8601", null);
        }
    }

    @GetMapping()
    public ResponseData<?> getLocations(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        Optional<Users> optionalUsers = userRepository.findUserByUsername(username);
        if (optionalUsers.isEmpty()) {
            return new ResponseData<>(401, "user not found", null);
        }
        Users user = optionalUsers.get();
        Optional<Pet> optionalPet = petRepository.findByUser(user);
        if (optionalPet.isEmpty()) {
            return new ResponseData<>(404, "pet not found", null);
        }
        Pet pet = optionalPet.get();

        Iterable<Location> locationIterable = locationRepository.findByPetOrderByRecordDateTimeDesc(pet);
        List<Location> locations = new ArrayList<>();
        locations.add(locationIterable.iterator().next());
        return new ResponseData<>(200, "location found", locations);
    }
}
