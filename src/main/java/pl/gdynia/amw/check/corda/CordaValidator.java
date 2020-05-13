package pl.gdynia.amw.check.corda;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.gdynia.amw.check.Validator;
import pl.gdynia.amw.check.dto.CheckEntry;
import pl.gdynia.amw.check.dto.CheckResult;
import pl.gdynia.amw.check.file.ConfigReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class CordaValidator implements Validator {

    private static Validator INSTANCE;

    @Override
    public CheckResult validateModel(Map<String, String> model, String configurationFile, Set<String> taggedValues) throws IOException {
        CheckResult result = new CheckResult();
        Map<String, String> config = new ConfigReader(configurationFile).getConfig();
        config.forEach((key, value) -> {
            CheckEntry entry = new CheckEntry();
            entry.setConfigValue(parseValue(value));

            String taggedValue = findTaggedValue(taggedValues, key);
            if (StringUtils.isBlank(taggedValue)) {
                entry.setField(key.trim());
                entry.setDifferenceType(CheckEntry.DifferenceType.NOT_IN_UML);
            } else {
                entry.setField(taggedValue);
                entry.setUmlValue(model.get(taggedValue));
                entry.setDifferenceType(entry.areTheSame() ?
                        CheckEntry.DifferenceType.NO_DIFFERENCE :
                        CheckEntry.DifferenceType.DIFFERENT_VALUE);
            }

            result.getResult().add(entry);
        });

        model.keySet()
                .stream()
                .filter(key -> !result.getResult().stream().anyMatch(entry -> entry.getField().equals(key)))
                .forEach(key -> {
                    CheckEntry entry = new CheckEntry();
                    entry.setField(key);
                    entry.setUmlValue(model.get(key));
                    entry.setDifferenceType(CheckEntry.DifferenceType.NOT_IN_CONFIG);
                    result.getResult().add(entry);
                });

        Collections.sort(result.getResult(), (Comparator) (object1, object2) -> ((CheckEntry) object1).getField().compareTo(((CheckEntry) object2).getField()));

        return result;
    }

    private String findTaggedValue(Set<String> taggedValues, String configKey) {
        String taggedValue = null;
        if (Character.isWhitespace(configKey.charAt(0))) {
            taggedValue = taggedValues.stream().filter(tv -> tv.endsWith("."+configKey.trim())).findFirst().orElse(null);
        } else {
            taggedValue = taggedValues.stream().filter(tv -> tv.equals(configKey.trim())).findFirst().orElse(null);
        }

        return taggedValue;
    }

    private String parseValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        value = value.trim();
        if (value.startsWith("<!--") && value.endsWith("-->")) {
            return null;
        }

        return value;
    }

    public static Validator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CordaValidator();
        }

        return INSTANCE;
    }
}
