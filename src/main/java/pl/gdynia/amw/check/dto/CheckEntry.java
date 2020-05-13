package pl.gdynia.amw.check.dto;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

public class CheckEntry {

    @Getter @Setter private String field;
    @Getter @Setter private String umlValue;
    @Getter @Setter private String configValue;
    @Getter @Setter private DifferenceType differenceType;

    public boolean areTheSame() {
        return StringUtils.equals(umlValue, configValue);
    }

    public enum DifferenceType {
        NO_DIFFERENCE(null),
        NOT_IN_UML("yellow"),
        NOT_IN_CONFIG("orange"),
        DIFFERENT_VALUE("red");

        @Getter private String color;

        DifferenceType(String color) {
            this.color = color;
        }


    }

}
