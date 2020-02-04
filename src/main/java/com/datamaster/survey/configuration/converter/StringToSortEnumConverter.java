package com.datamaster.survey.configuration.converter;

import com.datamaster.survey.service.SurveyFilter;
import org.springframework.core.convert.converter.Converter;

import javax.validation.constraints.NotNull;

/**
 *  Custom Data Binder which allows to create {@link com.datamaster.survey.service.SurveyFilter.Sort} enum
 *  using string constants in lover case.
 */
public class StringToSortEnumConverter implements Converter<String, SurveyFilter.Sort> {
    @Override
    public SurveyFilter.Sort convert(@NotNull String sortName) {
        return SurveyFilter.Sort.valueOf(sortName.toUpperCase());
    }
}
