package feedreader.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Validate {

    public static String getEmailRules() {
        return "The email needs to match, someone@domain.tld, tld may be of the newer domain names.";
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;

        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }

        return result;
    }

    public static String getScreenNameRules() {
        return " Needs to be at least 3 letters long, don't contain white spaces or special characters "
                        + "rather than '-' and '_' are allowed.";
    }

    public static boolean isValidScreenName(String name) {
        return (!name.isEmpty() && name.length() > 4 && !name.contains("~!@#$%^&*"));
    }

    public static String getPasswordRules() {
        return "Needs to be at least 4 letters long.";
    }

    public static boolean isValidPassword(String pwd) {
        if (!pwd.isEmpty() && pwd.length() > 3) {
            return true;
        }

        return false;
    }
}
