package ee.ituk.api.login;

import lombok.Data;

@Data
public class LoginUserDto {

    private String email;
    private String password;
}
