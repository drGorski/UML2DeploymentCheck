package pl.gdynia.amw.check.file;

import com.sun.istack.internal.NotNull;
import lombok.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {

    private static final String OBJECT_OPEN = "{";
    private static final String EQUALS_SIGN = "=";

    @NotNull private String source;
    @Getter private Map<String, String> config;

    public ConfigReader(String source) throws IOException {
        this.source = source;
        this.config = new HashMap<>();
        readConfig();
    }

    private void readConfig() throws IOException {
        FileUtils.readLines(FileUtils.getFile(source), Charset.forName("UTF-8"))
                .stream()
                .filter(line -> line.contains(EQUALS_SIGN))
                .forEach(line -> {
                    String[] elements = StringUtils.split(line, EQUALS_SIGN);
                    if (!elements[1].trim().equals(OBJECT_OPEN)) {
                        String key = elements[0];
                        String value = String.join(EQUALS_SIGN, Arrays.asList(Arrays.copyOfRange(elements, 1, elements.length)));
                        config.put(key, value);
                    }
                });
    }

}
