package org.anyone.backend.controller;

import org.anyone.backend.model.Activities;
import org.anyone.backend.model.Pet;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.ActivitiesRepository;
import org.anyone.backend.service.ActivitiesService;
import org.anyone.backend.service.PetService;
import org.anyone.backend.service.UserService;
import org.anyone.backend.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@RestController
@RequestMapping("/activities")
public class ActivitiesController {
    private final ActivitiesService activitiesService;
    private final UserService userService;
    private final PetService petService;
    private final Logger logger = LoggerFactory.getLogger(ActivitiesController.class);

    public ActivitiesController(ActivitiesService activitiesService, UserService userService, PetService petService) {
        this.activitiesService = activitiesService;
        this.petService = petService;
        this.userService = userService;
    }

    @GetMapping
    ResponseData<?> getActivities(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails
    ) {
        Users user = userService.getUser(userDetails);
        if (user == null) return ResponseData.userNotFoundResponse();
        Pet pet = petService.getPet(user);
        if (pet == null) return new ResponseData<>(404, "pet not found");

        ArrayList<Activities> activities = activitiesService.getActivitiesList(pet);
        return new ResponseData<>(200, "activities found", activities);
    }

    @GetMapping(params = "start")
    ResponseData<?> getActivities(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "start") String start
    ) {
        try {
            Users user = userService.getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = petService.getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            LocalDate startDT = LocalDate.parse(start);
            ArrayList<Activities> activities = activitiesService.getActivitiesList(pet, startDT);
            return new ResponseData<>(200, "activities found", activities);
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }
    }

    @GetMapping(params = {"start", "end"})
    ResponseData<?> getActivities(
            @CurrentSecurityContext(expression = "authentication.principal") UserDetails userDetails,
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end
    ) {
        try {
            Users user = userService.getUser(userDetails);
            if (user == null) return ResponseData.userNotFoundResponse();
            Pet pet = petService.getPet(user);
            if (pet == null) return new ResponseData<>(404, "pet not found");

            LocalDate startDT = LocalDate.parse(start);
            LocalDate endDT = LocalDate.parse(end);
            ArrayList<Activities> activities = activitiesService.getActivitiesList(pet, startDT, endDT);
            return new ResponseData<>(200, "activities found", activities);
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return ResponseData.badRequestBodyResponse();
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseData.serverFailureResponse();
        }

    }
}
