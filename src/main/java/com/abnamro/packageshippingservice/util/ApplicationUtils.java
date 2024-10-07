package com.abnamro.packageshippingservice.util;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@UtilityClass
public class ApplicationUtils {

    /**
     * Concatenates the provided first and last names into a single string.
     *
     * <p>The method trims any whitespace from the names and ignores null or blank values.
     * If both names are null or blank, an empty string is returned.
     *
     * @param firstName the first name to concatenate; can be null or blank
     * @param lastName  the last name to concatenate; can be null or blank
     * @return a concatenated string of the first and last names, separated by a space;
     * returns an empty string if both names are null or blank
     */
    public static String concatNames(String firstName, String lastName) {
        return Stream.of(firstName, lastName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .reduce((firstname, lastname) -> String.join(" ", firstname, lastname))
                .orElse("");
    }

    /**
     * Calculates the expected delivery date based on the number of days required to process.
     *
     * <p>The expected delivery date is computed as the current date plus the specified
     * number of processing days, adjusted to the next or same Monday.
     *
     * @param daysToProcess the number of days required for processing
     * @return the calculated expected delivery date as a {@link LocalDate}
     */
    public static LocalDate calculateExpectedDeliveryDate(int daysToProcess) {
        return LocalDate.now().plusDays(daysToProcess).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    }
}

