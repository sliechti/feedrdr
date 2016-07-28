package feedreader.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class Validate {

    public static String getEmailRules() {
        return "The email needs to match, someone@domain.tld, tld may be of the newer domain names.";
    }

    public static boolean isValidEmailAddress(String email) {
        return EmailValidator.getInstance(false, true).isValid(email);
    }

    public static String getScreenNameRules() {
        return " Needs to be at least 3 letters long, don't contain white spaces or special characters "
                + "rather than '-' and '_' are allowed.";
    }

    public static boolean isValidScreenName(String name) {
        return (!name.isEmpty() && name.length() > 4 && !name.contains("~!@#$%^&*"));
    }

    public static String getPasswordRules() {
        return "Passwords need to be at least 4 letters long.";
    }

    public static boolean isValidPassword(String pwd) {
        return (!pwd.isEmpty() && pwd.length() > 3) ? true : false;
    }
}
