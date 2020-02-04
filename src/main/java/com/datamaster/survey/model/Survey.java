package com.datamaster.survey.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private Boolean isActive;

    @OneToMany(mappedBy = "survey", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder")
    private Collection<Question> questions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Survey setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Survey setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean isActive() {
        return isActive;
    }

    public Survey setActive(Boolean active) {
        isActive = active;
        return this;
    }

    public Collection<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Collection<Question> otherQuestions) {
        questions.clear();
        questions.addAll(otherQuestions);
        questions.forEach(q -> q.setSurvey(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Survey survey = (Survey) o;
        return isActive == survey.isActive &&
                id.equals(survey.id) &&
                name.equals(survey.name) &&
                startDate.equals(survey.startDate) &&
                Objects.equals(endDate, survey.endDate) &&
                Objects.equals(questions, survey.questions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, isActive, questions);
    }

    public void updateFrom(Survey survey) {
        setName(survey.getName());
        setStartDate(survey.getStartDate());
        setEndDate(survey.getEndDate());
        setActive(survey.isActive());
        setQuestions(survey.getQuestions());
    }
}
