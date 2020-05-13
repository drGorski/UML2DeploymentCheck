package pl.gdynia.amw.check;

import pl.gdynia.amw.check.dto.CheckResult;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface Validator {

    CheckResult validateModel(Map<String, String> model, String configurationFile, Set<String> taggedValues) throws IOException;

}
