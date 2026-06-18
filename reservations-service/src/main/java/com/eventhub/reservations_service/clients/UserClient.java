package com.eventhub.reservations_service.clients;

import com.eventhub.reservations_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.eventhub.reservations_service.config.FeignClientConfig;

@FeignClient(name = "USERS-SERVICE", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") String id);
}