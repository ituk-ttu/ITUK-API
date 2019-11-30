package ee.ituk.api.meeting;

import ee.ituk.api.meeting.dto.GeneralMeetingDto;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("meeting")
@RequiredArgsConstructor
public class GeneralMeetingController {

    private final GeneralMeetingService meetingService;

    private final GeneralMeetingMapper mapper = Mappers.getMapper(GeneralMeetingMapper.class);

    @GetMapping
    public ResponseEntity getAllMeetings() {
        return ok(meetingService.getAll());
    }

    @PostMapping
    public ResponseEntity createMeeting(@RequestBody GeneralMeetingDto meetingDto) {
        return ok(mapper.meetingToDto(meetingService.create(mapper.dtoToMeeting(meetingDto))));
    }

    @PutMapping("{id}")
    public ResponseEntity updateMeeting(@PathVariable Long id, @RequestBody GeneralMeetingDto meetingDto) {
        return ok(mapper.meetingToDto(meetingService.update(id, mapper.dtoToMeeting(meetingDto))));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteMeeting(@PathVariable Long id) {
        meetingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}