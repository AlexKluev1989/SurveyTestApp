package com.datamaster.survey.controller;

import com.datamaster.survey.model.Survey;
import com.datamaster.survey.service.SurveyFilter;
import com.datamaster.survey.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Main API controller
 */
@RestController
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @GetMapping("/surveys")
    public Page<Survey> getAllSurveys(@Valid SurveyFilter filter) {
        return surveyService.find(filter);
    }

    @RequestMapping(path = "/survey", method = {RequestMethod.POST, RequestMethod.PUT}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addOrUpdate(@RequestBody Survey survey) {
        surveyService.saveOrUpdate(survey);
    }
}
