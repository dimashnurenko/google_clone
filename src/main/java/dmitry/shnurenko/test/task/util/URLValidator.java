package dmitry.shnurenko.test.task.util;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class provides method which allows to validate URL in according to URL convention.
 *
 * @author Dmitry Shnurenko
 */
public class URLValidator {

    private static final Pattern URL_PATTERN = Pattern.compile("(https?:\\/\\/(?:www\\.|(?!www))[^\\s\\.]+\\.[^\\s]" +
                                                               "{2,}|www\\.[^\\s]+\\.[^\\s]{2,})");

    private URLValidator() {
        throw new UnsupportedOperationException("Can't create instance of util class: " + getClass());
    }

    /**
     * Validates passed URL in according to special URL pattern.
     *
     * @param url URL which will be validated
     * @return <code>true</code> if URL is valid, and <code>false</code> otherwise
     */
    public static boolean isUrlValid(@NonNull String url) {
        Matcher matcher = URL_PATTERN.matcher(url);

        return matcher.matches();
    }
}
