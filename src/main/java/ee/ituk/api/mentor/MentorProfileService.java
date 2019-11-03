package ee.ituk.api.mentor;

import ee.ituk.api.common.exception.NotFoundException;
import ee.ituk.api.mentor.domain.MentorProfile;
import ee.ituk.api.user.UserRepository;
import ee.ituk.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;

    public MentorProfile createProfile(MentorProfile mentorProfile) {
        return mentorProfileRepository.save(mentorProfile);
    }

    public List<MentorProfile> getAll() {
        return mentorProfileRepository.findAll();
    }

    public MentorProfile getByUserId(long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        return mentorProfileRepository.findByUser(user).orElseThrow(NotFoundException::new);
    }

    public MentorProfile findByUser(User user) {
        return mentorProfileRepository.findByUser(user).orElseThrow(NotFoundException::new);
    }

    public MentorProfile updateMentor(MentorProfile mentorprofile) {
        return mentorProfileRepository.save(mentorprofile);
    }

    public void deleteMentorProfile(long id) {
        MentorProfile mentorProfile = getByUserId(id);
        mentorProfileRepository.delete(mentorProfile);
    }

    public List<MentorProfile> getAllActive() {
        return mentorProfileRepository.findAll().stream()
                .filter(profile -> profile.getUser().getRole().isMentor())
                .collect(Collectors.toList());
    }

    public void create(User user) {
        MentorProfile profile = new MentorProfile(user);
        mentorProfileRepository.save(profile);
    }

}
