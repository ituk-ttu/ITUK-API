package ee.ituk.api.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Door {

  @Id
  private String code;

}
