package ee.ituk.api.common.validation;

import ee.ituk.api.common.exception.ValidationException;

public final class ValidationUtil {

    public static final String PASSWORD_INCORRECT = "password.incorrect";
    public static final String PASSWORD_NOT_SECURE = "password.not.secure";
    public static final String PASSWORD_UNCHANGED = "password.unchanged";

    public static final String NAME_MISSING = "name.missing";

    public static final String PROJECT_MISSING_REQUIRED_FIELDS = "project.missing.required.fields";
    public static final String PROJECT_MEMBER_NOT_VALID = "project.member.not.valid";

    private ValidationUtil() {
        //not instance
    }

    public static void checkForErrors(ValidationResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
    }
}
