package com.datamaster.survey.service;

import com.datamaster.survey.model.Survey;
import com.datamaster.survey.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Service to handle {@link Survey} entity.
 * TODO Implement tests for this service
 */
@Service
public class SurveyService {
    @Autowired
    private SurveyRepository repository;

    public Page<Survey> find(SurveyFilter f) {
        if (f.getSortBy() == null) {
            throw new IllegalArgumentException("sortBy param must be specified. Accepted value is 'name' or 'date'");
        }

        Pageable p = PageRequest.of(f.getPage(), f.getPageSize(), createSort(f));
        return repository.findAll(crateExample(f), p);
    }

    private static Example<Survey> crateExample(SurveyFilter f) {
        return Example.of(
                new Survey().setName(f.getName())
                            .setStartDate(f.getStartDate())
                            .setActive(f.isActive()));
    }

    private static Sort createSort(SurveyFilter f) {
        return Sort.by(f.getSortBy().getFieldName());
    }

    /**
     * Check all required field and throw IAE if any is not present
     * @param s survey entity which is going to be saved/updated
     */
    private void validate(Survey s) {
        if (s == null) {
            throw new IllegalArgumentException("Survey should not be null");
        }

        if (s.getName() == null || s.getName().isEmpty()) {
            throw new IllegalArgumentException("Name should be not empty");
        }

        if (s.getStartDate() == null) {
            throw new IllegalArgumentException("Start date should not be null");
        }
    }


    /**
     * TODO write comment
     * @param survey
     */
    @Transactional
    public void saveOrUpdate(@NotNull Survey survey) {
        validate(survey);

        if (survey.getId() == null) {
            save(survey);
        } else {
            update(survey);
        }
    }

    private void save(Survey survey) {
        survey.getQuestions().forEach(q -> q.setSurvey(survey));
        repository.save(survey);
    }

    private void update(Survey survey) {
        Optional<Survey> os = repository.findById(survey.getId());
        if (!os.isPresent()) {
            save(survey);
            return;
        }

        Survey surveyInDb = os.get();
        surveyInDb.updateFrom(survey);
        repository.save(surveyInDb);
    }

}
