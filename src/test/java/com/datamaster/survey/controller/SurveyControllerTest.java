package com.datamaster.survey.controller;

import com.datamaster.survey.service.SurveyService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class SurveyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyService surveyService;

    @ParameterizedTest
    @MethodSource("surveyApiTestArgsProvider")
    void getSurveysTest(MultiValueMap<String, String> params, int expectedStatus) throws Exception {
        mockMvc.perform(get("/surveys").params(params)
                .contentType("application/json"))
                .andExpect(status().is(expectedStatus));
    }

    static Stream<Arguments> surveyApiTestArgsProvider() {
        return Stream.of(
                // Valid params
                Arguments.of(createTestSet("sur", null, "true", "date"), 200),
                Arguments.of(createTestSet(null, "2020-01-30", "false", "name"), 200),
                Arguments.of(createTestSet(null, null, null, "name"), 200),

                // Invalid date
                Arguments.of(createTestSet(null, "2020-99-99", null, "name"), 400),
                // null sortBy
                Arguments.of(createTestSet(null, null, null, null), 400));
    }

    static MultiValueMap<String, String> createTestSet(String name, String startDate, String active, String sortBy) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (name != null) {
            map.add("name", name);
        }

        if (startDate != null) {
            map.add("startDate", startDate);
        }

        if (active != null) {
            map.add("active", active);
        }

        map.add("sortBy", sortBy);
        return map;
    }
}