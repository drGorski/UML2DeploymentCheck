package pl.gdynia.amw.check.file;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConfigReader {

    private static final String OBJECT_OPEN = "{";
    private static final String EQUALS_SIGN = "=";
    private static final String ZIP_EXTENSION = "zip";

    @NotNull private String source;
    @Getter private Map<String, String> config;
    private String configFile;

    public ConfigReader(String source) throws IOException {
        this.source = source;
        this.config = new HashMap<>();
        if (source.endsWith(ZIP_EXTENSION)) {
            extractConfigFileFromNodeZipPackage();
        } else {
            extractConfigDirectlyFromConfigFile();
        }

    }

    private void extractConfigFileFromNodeZipPackage() throws IOException {
        ZipFile zipFile = new ZipFile(source);
        ZipEntry zipEntry = zipFile.getEntry("node.config");
        readConfig(IOUtils.readLines(zipFile.getInputStream(zipEntry), Charset.forName("UTF-8")));
    }

    private void extractConfigDirectlyFromConfigFile() throws IOException {
        readConfig(FileUtils.readLines(FileUtils.getFile(source), Charset.forName("UTF-8")));

    }

    private void readConfig(List<String> lines) {
        lines.stream()
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
