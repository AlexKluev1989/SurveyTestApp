package com.datamaster.survey.service;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Special class to handle controller's parameters
 */
public class SurveyFilter {
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    private Boolean active;

    @NotNull
    private Sort sortBy;

    private int page;

    private int pageSize = Integer.MAX_VALUE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Sort getSortBy() {
        return sortBy;
    }

    public void setSortBy(Sort sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public enum Sort {
        NAME("name"),
        DATE("startDate");

        Sort(String fieldName) {
            this.fieldName = fieldName;
        }

        private String fieldName;

        public String getFieldName() {
            return fieldName;
        }
    }

}
