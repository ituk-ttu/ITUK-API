package ee.ituk.api.join;

import ee.ituk.api.join.dto.ApplicationDto;
import ee.ituk.api.join.dto.ApplicationResponseDto;
import ee.ituk.api.join.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper = Mappers.getMapper(ApplicationMapper.class);

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteApplication(@PathVariable long id) {
        applicationService.deleteApplication(id);
    }

    @PutMapping
    @ResponseBody
    public ApplicationDto updateApplication( ApplicationDto applicationDto) {
        return applicationMapper.applicationToDto(
                applicationService.updateProfile(
                        applicationMapper.applicationToEntity(applicationDto)));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ApplicationResponseDto findApplicationById(@PathVariable long id) {
        return applicationMapper.applicationToResponseDto(applicationService.findApplicationById(id));
    }

    @GetMapping
    @ResponseBody
    public List<ApplicationResponseDto> findAllApplications() {
        return applicationMapper.applicationsToResponseDto(applicationService.findAll());
    }

    @PostMapping
    public ApplicationDto createApplication(@RequestBody ApplicationDto applicationDto) {
        return applicationMapper.applicationToDto(
                applicationService.createApplication(
                        applicationMapper.applicationToEntity(applicationDto)));
    }
}
