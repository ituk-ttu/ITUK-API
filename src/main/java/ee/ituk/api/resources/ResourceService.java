package ee.ituk.api.resources;

import ee.ituk.api.resources.domain.Resource;
import ee.ituk.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final UserService userService;

    List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    Resource saveResource(Resource resource) {
        if (Objects.isNull(resource.getId())) {
            resource.setCreatedAt(LocalDateTime.now());
        }
        resource.setAuthor(userService.findUserById(resource.getAuthor().getId()));
        resource.setUpdatedAt(LocalDateTime.now());
        return resourceRepository.save(resource);
    }
}
