package ee.ituk.api.recovery;

import ee.ituk.api.exception.NotFoundException;
import ee.ituk.api.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RecoveryService {

    private final RecoveryKeyRepository recoveryKeyRepository;

    public User getUserByKey(String key) {
        return recoveryKeyRepository.findByKey(key)
                .map(RecoveryKey::getUser)
                .orElseThrow(NotFoundException::new);
    }

    public RecoveryKey createRecoveryKey(User user) {
        RecoveryKey recoveryKey = new RecoveryKey();
        recoveryKey.setUser(user);
        recoveryKey.setKey(UUID.randomUUID().toString().replace("-", ""));
        return recoveryKeyRepository.save(recoveryKey);
    }
}