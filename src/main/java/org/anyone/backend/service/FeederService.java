package org.anyone.backend.service;

import org.anyone.backend.model.Feeder;
import org.anyone.backend.model.Users;
import org.anyone.backend.repository.FeederRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeederService {
    private final FeederRepository feederRepository;
    private final Logger logger = LoggerFactory.getLogger(FeederService.class);

    public FeederService(FeederRepository feederRepository) {
        this.feederRepository = feederRepository;
    }

    public Feeder getFeeder(Users user) {
        Optional<Feeder> optionalFeeder = feederRepository.findByUser(user);
        return optionalFeeder.orElse(null);
    }
}
