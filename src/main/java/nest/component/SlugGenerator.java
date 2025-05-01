package nest.component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class SlugGenerator {
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String generateSlug(String author, String title, Long id) {
        String baseSlug = (author + "-" + title + "-" + id).toLowerCase(Locale.ROOT);
        String noWhitespace = WHITESPACE.matcher(baseSlug).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        return NON_LATIN.matcher(normalized).replaceAll("");
    }
}
