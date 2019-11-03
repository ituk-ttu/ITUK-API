package ee.ituk.api.project.service;

import ee.ituk.api.common.exception.NotFoundException;
import ee.ituk.api.project.domain.Project;
import ee.ituk.api.project.repository.ProjectRepository;
import ee.ituk.api.project.validation.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static ee.ituk.api.common.validation.ValidationUtil.checkForErrors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectValidator validator = new ProjectValidator();

    public Project findById(long id) {
        return projectRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }


    public Project save(Project project) {
        checkForErrors(validator.validateData(project));

        selfMapProject(project);

        project = projectRepository.save(project);
        return project;
    }

    public void delete(long id) {
        projectRepository.deleteById(id);
    }

    private void selfMapProject(Project project) {
        project.getSummary().setProject(project);
        project.getBudget().setProject(project);
        project.getMembers().forEach(member -> member.setProject(project));
        project.getBudget().getRows().forEach(row -> row.setProjectBudget(project.getBudget()));
    }
}
