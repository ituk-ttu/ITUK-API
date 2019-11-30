package ee.ituk.api.meeting.general;

import ee.ituk.api.common.exception.ErrorMessage;
import ee.ituk.api.common.exception.NotFoundException;
import ee.ituk.api.common.exception.ValidationException;
import ee.ituk.api.common.validation.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ee.ituk.api.common.validation.ValidationUtil.*;

@Service
@RequiredArgsConstructor
public class GeneralMeetingService {

    private final GeneralMeetingsRepository meetingsRepository;

    private final GeneralMeetingValidator meetingValidator = new GeneralMeetingValidator();

    public GeneralMeeting create(GeneralMeeting meeting) {
        checkForErrors(meetingValidator.validateOnCreate(meeting));
        return meetingsRepository.save(meeting);
    }

    public void delete(Long id) {
        GeneralMeeting meeting = meetingsRepository.findById(id).orElseThrow(
                () -> new NotFoundException(Collections.singletonList(ValidationUtil.getNotFoundError(this.getClass()))));
        meetingsRepository.delete(meeting);
    }

    List<GeneralMeeting> getAll() {
        List<GeneralMeeting> meetings = meetingsRepository.findAll();
        meetings.sort(Comparator.comparing(GeneralMeeting::getDate).reversed());
        return meetings;
    }

    GeneralMeeting update(Long id, GeneralMeeting meeting) {
        meetingsRepository.findById(id).orElseThrow(
                () -> new NotFoundException(Collections.singletonList(ValidationUtil.getNotFoundError(this.getClass()))));
        if (!id.equals(meeting.getId())) {
            throw new ValidationException(ErrorMessage.builder().code(MEETING_ID_MISMATCH).build());
        }
        return meetingsRepository.save(meeting);
    }

    public GeneralMeeting findById(Long id) {
        return meetingsRepository.findById(id).orElseThrow(
                () -> new NotFoundException(Collections.singletonList(getNotFoundError(this.getClass())))
        );
    }
}