package ee.ituk.api.join;

import ee.ituk.api.user.domain.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "application", schema = "public")
public class Application {

  @Id
  @GeneratedValue
  private Long id;
  private String firstName;
  private String lastName;
  private String personalCode;
  private String email;
  private String studentCode;
  private String curriculum;
  private String mentorSelectionCode;
  private LocalDate createdAt;
  private LocalDate updatedAt;
  @ManyToOne
  @JoinColumn(name = "processed_by_id")
  private User processedBy;
  @ManyToOne
  @JoinColumn(name = "mentor_id")
  private MentorProfile mentor;
}
