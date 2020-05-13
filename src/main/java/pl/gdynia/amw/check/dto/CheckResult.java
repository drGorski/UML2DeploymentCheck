package pl.gdynia.amw.check.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class CheckResult {

    @Getter  private List<CheckEntry> result = new ArrayList<>();

}
