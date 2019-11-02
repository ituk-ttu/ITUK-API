package ee.ituk.api.login;

import ee.ituk.api.common.exception.IncorrectPasswordException;
import ee.ituk.api.config.security.JwtTokenUtil;
import ee.ituk.api.user.UserService;
import ee.ituk.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final SessionService sessionService;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder encoder;

    String loginUser(LoginUserDto loginUserDto) {
        User user = userService.loadInternalUserByUsername(loginUserDto.getEmail());
        if (encoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            sessionService.createSession(user);
            return jwtTokenUtil.generateToken(user);
        } else {
            throw new IncorrectPasswordException();
        }
    }
}