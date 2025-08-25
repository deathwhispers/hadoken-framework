package com.hadoken.framework.stats.period.adjuster;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;

/**
 * 自定义季度调整器
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:00
 */
public class QuarterAdjusters {

    public static TemporalAdjuster firstDayOfQuarter() {
        return temporal -> {
            LocalDate date = LocalDate.from(temporal);
            int month = date.getMonthValue();
            int year = date.getYear();
            int firstMonth = (month - 1) / 3 * 3 + 1; // 1, 4, 7, 10
            return temporal.with(LocalDate.of(year, firstMonth, 1));
        };
    }

    public static TemporalAdjuster lastDayOfQuarter() {
        return temporal -> {
            LocalDate date = LocalDate.from(temporal);
            int month = date.getMonthValue();
            int year = date.getYear();
            int firstMonth = (month - 1) / 3 * 3 + 1;
            LocalDate start = LocalDate.of(year, firstMonth, 1);
            LocalDate end = start.plusMonths(2).with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
            return temporal.with(end);
        };
    }

    /**
     * 获取上个季度的第一天
     */
    public static TemporalAdjuster firstDayOfPreviousQuarter() {
        return temporal -> {
            LocalDate date = LocalDate.from(temporal).minusMonths(3);
            int month = date.getMonthValue();
            int year = date.getYear();
            int firstMonth = (month - 1) / 3 * 3 + 1;
            return temporal.with(LocalDate.of(year, firstMonth, 1));
        };
    }

    /**
     * 获取上个季度的最后一天
     */
    public static TemporalAdjuster lastDayOfPreviousQuarter() {
        return temporal -> {
            LocalDate date = LocalDate.from(temporal).minusMonths(3);
            int month = date.getMonthValue();
            int year = date.getYear();
            int firstMonth = (month - 1) / 3 * 3 + 1;
            LocalDate start = LocalDate.of(year, firstMonth, 1);
            LocalDate end = start.plusMonths(2).with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
            return temporal.with(end);
        };
    }
}