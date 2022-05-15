package com.ead.course.clients;

import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.ResponsePageDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.services.UtilsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUserClient {

    private final UtilsServiceImpl utilsService;

    private final RestTemplate restTemplate;

    @Value("${ead.api.url.auth-user}")
    String REQUEST_URI;

    public Page<UserDTO> getAllCoursesByUser(UUID courseId, Pageable pageable) {
        List<UserDTO> searchResult = null;
        String url = REQUEST_URI + utilsService.createUrlGetAllUsersByCourse(courseId, pageable);
        log.debug("Rest Url {}", url);
        log.info("Rest Url {}", url);

        try {
            ParameterizedTypeReference<ResponsePageDTO<UserDTO>> responseType = new ParameterizedTypeReference<ResponsePageDTO<UserDTO>>() {
            };
            ResponseEntity<ResponsePageDTO<UserDTO>> result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();

            log.debug("error {}", searchResult.size());
        } catch (HttpStatusCodeException e) {
            log.error("Error request/ courses {}", e);
        }
        log.info("Ending request/ courses {}", courseId);
        return new PageImpl<>(searchResult);

    }

    public ResponseEntity<UserDTO> getUserById(UUID userId) {
        String url = REQUEST_URI + "/users/" + userId;

        return restTemplate.exchange(url, HttpMethod.GET, null, UserDTO.class);
    }

    public void postSubscriptionInCourse(UUID courseId, UUID userId) {
        String url = REQUEST_URI + "/users/" + userId + "/courses/subscription";

        var courseUser = CourseUserDTO.builder()
                .courseId(courseId)
                .userId(userId).build();
        restTemplate.postForObject(url, courseUser, String.class);

    }
}
