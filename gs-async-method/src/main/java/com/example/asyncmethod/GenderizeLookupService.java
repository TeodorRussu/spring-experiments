package com.example.asyncmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class GenderizeLookupService {

	private static final Logger logger = LoggerFactory.getLogger(GenderizeLookupService.class);

	private final RestTemplate restTemplate;

	public GenderizeLookupService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Async
	public CompletableFuture<User> findUser(String user) throws InterruptedException {
		User results = findUserSync(user);
		return CompletableFuture.completedFuture(results);
	}


	public User findUserSync(String user)  {
		String url = String.format("https://api.genderize.io/?name==%s", user);
		User results = restTemplate.getForObject(url, User.class);
		return results;
	}
}
