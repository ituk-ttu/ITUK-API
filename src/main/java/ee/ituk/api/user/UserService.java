package ee.ituk.api.user;

import ee.ituk.api.application.domain.Application;
import ee.ituk.api.application.repository.ApplicationRepository;
import ee.ituk.api.common.exception.BadCredentialsException;
import ee.ituk.api.common.exception.NotFoundException;
import ee.ituk.api.common.exception.ValidationException;
import ee.ituk.api.login.SessionService;
import ee.ituk.api.mentor.MentorProfileRepository;
import ee.ituk.api.mentor.MentorProfileService;
import ee.ituk.api.user.domain.Role;
import ee.ituk.api.user.domain.User;
import ee.ituk.api.user.dto.PasswordChangeDto;
import ee.ituk.api.user.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ee.ituk.api.common.validation.ValidationUtil.checkForErrors;
import static ee.ituk.api.common.validation.ValidationUtil.getNotFoundError;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final ApplicationRepository applicationRepository;
    private final MentorProfileService mentorProfileService;
    private final SessionService sessionService;
    private final BCryptPasswordEncoder encoder;

    private final UserValidator userValidator = new UserValidator();

    @Override
    public UserDetails loadUserByUsername(String email) {
        return loadInternalUserByUsername(email);
    }

    public User loadInternalUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(BadCredentialsException::new);
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new NotFoundException(Collections.singletonList(getNotFoundError(this.getClass()))));
    }

    public List<User> findByIds(List<Long> ids) {
        return userRepository.findByIdIn(ids);
    }

    void changePassword(long id, PasswordChangeDto passwordChangeDto) {
        User user = findUserById(id);
        if (encoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())) {
            user.setPassword(encoder.encode(passwordChangeDto.getNewPassword()));
            checkForErrors(userValidator.validatePasswordChange(passwordChangeDto));
            saveUser(user);
        } else {
            throw new BadCredentialsException();
        }
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByIdAsc();
    }

    User createUser(User user) {
        checkForErrors(userValidator.validateOnCreate(user));
        User savedUser = saveUser(user);
        if (savedUser.isMentor() && mentorProfileRepository.findByUser(savedUser).isEmpty()) {
            mentorProfileService.create(savedUser);
        }
        return savedUser;
    }

    void logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = loadInternalUserByUsername(username);
        sessionService.deleteSession(user);
    }

    long getMemberCount() {
        return userRepository.count();
    }

    User updateUser(User user) {
        //TODO validation
        User fromBase = userRepository.getOne(user.getId());
        user.setPassword(fromBase.getPassword());
        if (user.isMentor() && mentorProfileRepository.findByUser(user).isEmpty()) {
            mentorProfileService.create(user);
        }
        return userRepository.save(user);
    }

    User getLoggedUser() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new NotFoundException(Collections.singletonList(getNotFoundError(this.getClass()))));
    }

    void changeRole(Long id, Role role) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        user.setRole(role);
        userRepository.save(user);
        if (user.isMentor() && mentorProfileRepository.findByUser(user).isEmpty()) {
            mentorProfileService.create(user);
        }
    }

    List<String> getBirthdayUserNames() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> {
                    if (user.getPersonalCode() != null) {
                        int month = Integer.parseInt(user.getPersonalCode().substring(3, 5));
                        int day = Integer.parseInt(user.getPersonalCode().substring(5, 7));
                        return LocalDate.now().getMonthValue() == month && LocalDate.now().getDayOfMonth() == day;
                    }
                    return false;
                })
                .map(User::getFullName)
                .collect(Collectors.toList());
    }

    void archive(Long id, boolean isArchived) {
        User user = userRepository.findById(id).orElseThrow(() -> new ValidationException(getNotFoundError(User.class)));
        user.setArchived(isArchived);
        userRepository.save(user);
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }

    public String getMentorName(Long userId) {
        User user = findUserById(userId);
        Optional<Application> applicationOptional = applicationRepository.findByUser(user);
        if (applicationOptional.isPresent()) {
            return applicationOptional.get().getMentor().getFullName();
        }
        return "";
    }
}
