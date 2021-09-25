package com.example.asyncmethod;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final GenderizeLookupService genderizeLookupService;

    public AppRunner(GenderizeLookupService genderizeLookupService) {
        this.genderizeLookupService = genderizeLookupService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> inputData = Stream.generate(this::generateRandomName).limit(200).collect(Collectors.toList());
        var syncElapsedTime = runSync(inputData);
        logger.info("Sync elapsed time: " + (syncElapsedTime));
        var asyncElapsedTime = runAsync(inputData);
        logger.info("Async elapsed time: " + (asyncElapsedTime));

    }

    private String generateRandomName() {
        return Faker.instance().name().firstName();
    }

    private long runAsync(List<String> names) throws InterruptedException, java.util.concurrent.ExecutionException {
        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        CompletableFuture<User> [] futures = new CompletableFuture[names.size()];

        for (int i = 0; i < names.size(); i++) {
            futures[i] = genderizeLookupService.findUser(names.get(i));
        }

        // Wait until they are all done
        CompletableFuture.allOf(futures).join();

        // return elapsed time
        return System.currentTimeMillis() - start;

    }

    private long runSync(List<String> names) throws InterruptedException {
        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        for (String name : names) {
            genderizeLookupService.findUserSync(name);
        }

        return System.currentTimeMillis() - start;
    }

}
