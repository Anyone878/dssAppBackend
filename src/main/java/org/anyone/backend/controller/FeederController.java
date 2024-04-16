package org.anyone.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.anyone.backend.model.Feeder;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.FeederRepository;
import org.anyone.backend.repository.UserRepository;
import org.anyone.backend.service.FeederService;
import org.anyone.backend.service.FeedingSchedulesService;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feeder")
public class FeederController {

    private final UserRepository userRepository;
    private final FeederRepository feederRepository;
    private final FeedingSchedulesService feedingSchedulesService;
    private final Logger logger = LoggerFactory.getLogger(FeederController.class);

    public FeederController(UserRepository userRepository, FeederRepository feederRepository, FeedingSchedulesService feedingSchedulesService) {
        this.userRepository = userRepository;
        this.feederRepository = feederRepository;
        this.feedingSchedulesService = feedingSchedulesService;
    }

    @GetMapping
    public ResponseData<?> getFeeder(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Users> optionalUsers = userRepository.findUserByUsername(username);
        if (optionalUsers.isEmpty()) {
            return new ResponseData<>(401, "user not found", null);
        }

        Users user = optionalUsers.get();
        Optional<Feeder> optionalFeeder = feederRepository.findByUser(user);
        if (optionalFeeder.isEmpty()) {
            return new ResponseData<>(404, "feeder not found", null);
        }

        Feeder feeder = optionalFeeder.get();
        return new ResponseData<>(200, "feeder found", feeder);
    }

    /**
     * Need a string representing the updated data, like "100.0".
     */
    @PutMapping("/everyday_food_plan")
    ResponseData<?> updateFeeder(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestBody String everydayFoodPlanString
    ) {
        Optional<Users> optionalUsers = userRepository.findUserByUsername(userDetails.getUsername());
        if (optionalUsers.isEmpty()) {
            return ResponseData.userNotFoundResponse();
        }
        Users user = optionalUsers.get();

        Optional<Feeder> optionalFeeder = feederRepository.findByUser(user);
        if (optionalFeeder.isEmpty()) {
            return new ResponseData<>(404, "feeder not found");
        }
        Feeder feeder = optionalFeeder.get();

        try {
            float everydayFoodPlan = Float.parseFloat(everydayFoodPlanString);
            feeder.setEverydayFoodPlan(everydayFoodPlan);
            feederRepository.save(feeder);
            // update feeding amount
            feedingSchedulesService.updateAmount(user);
            return new ResponseData<>(200, "feeder updated", feeder);
        } catch (NumberFormatException e) {
            logger.error(e.toString());
            return ResponseData.badRequestBodyResponse();
        }
    }
}
