package apsoftware.operationsboard.dto;

public class CompletionTrendDto {

    private int year;
    private int month;
    private String monthLabel;
    private long count;

    public CompletionTrendDto() {
    }

    public CompletionTrendDto(int year, int month, String monthLabel, long count) {
        this.year = year;
        this.month = month;
        this.monthLabel = monthLabel;
        this.count = count;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public void setMonthLabel(String monthLabel) {
        this.monthLabel = monthLabel;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
