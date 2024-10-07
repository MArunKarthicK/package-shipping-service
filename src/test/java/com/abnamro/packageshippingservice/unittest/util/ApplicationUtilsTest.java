package com.abnamro.packageshippingservice.unittest.util;

import com.abnamro.packageshippingservice.util.ApplicationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ApplicationUtilsTest {

    @Test
    public void testConcatNames_withValidNames() {
        String result = ApplicationUtils.concatNames("John", "Doe");
        assertEquals("John Doe", result);
    }

    @Test
    public void testConcatNames_withNullFirstName() {
        String result = ApplicationUtils.concatNames(null, "Doe");
        assertEquals("Doe", result);
    }

    @Test
    public void testConcatNames_withNullLastName() {
        String result = ApplicationUtils.concatNames("John", null);
        assertEquals("John", result);
    }

    @Test
    public void testConcatNames_withBothNamesNull() {
        String result = ApplicationUtils.concatNames(null, null);
        assertEquals("", result);
    }

    @Test
    public void testConcatNames_withEmptyNames() {
        String result = ApplicationUtils.concatNames("", " ");
        assertEquals("", result);
    }

    @Test
    public void testConcatNames_withTrimming() {
        String result = ApplicationUtils.concatNames("  John  ", "  Doe  ");
        assertEquals("John Doe", result);
    }

    @Test
    public void testCalculateExpectedDeliveryDate() {
        int daysToProcess = 3; // For example, if today is Friday
        LocalDate expectedDate = LocalDate.now().plusDays(3).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        LocalDate result = ApplicationUtils.calculateExpectedDeliveryDate(daysToProcess);

        assertEquals(expectedDate, result);
    }

    @Test
    public void testCalculateExpectedDeliveryDate_zeroDays() {
        int daysToProcess = 0; // If today is Monday
        LocalDate expectedDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        LocalDate result = ApplicationUtils.calculateExpectedDeliveryDate(daysToProcess);

        assertEquals(expectedDate, result);
    }

    @Test
    public void testCalculateExpectedDeliveryDate_negativeDays() {
        int daysToProcess = -1; // Test with negative days
        LocalDate expectedDate = LocalDate.now().plusDays(-1).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

        LocalDate result = ApplicationUtils.calculateExpectedDeliveryDate(daysToProcess);

        assertEquals(expectedDate, result);
    }
}
