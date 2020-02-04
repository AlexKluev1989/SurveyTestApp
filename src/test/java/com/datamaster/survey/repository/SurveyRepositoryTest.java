package com.datamaster.survey.repository;

import com.datamaster.survey.model.Question;
import com.datamaster.survey.model.Survey;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionTemplate;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ContextConfiguration(classes=SurveyRepositoryTest.TestConfig.class)
@ActiveProfiles("test")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("classpath:/datasets/SurveyRepositoryTest-dataset.xml")
@DatabaseTearDown(value = "classpath:/datasets/SurveyRepositoryTest-dataset-clean.xml", type = DatabaseOperation.DELETE_ALL)
public class SurveyRepositoryTest {

    @Autowired
    private SurveyRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EntityManager em;

    @Test
    public void addNewTest() {
        Question q = new Question();
        q.setQuestionText("Test question text");

        Survey s = new Survey();
        s.setName("test survey");
        s.setStartDate(LocalDate.now());
        s.setQuestions(Collections.singleton(q));

        s = repository.save(s);

        assertNotNull(s);
        assertNotNull(s.getId());
        assertNotNull(s.getQuestions());
        assertFalse(s.getQuestions().isEmpty());
        assertNotNull(s.getQuestions().iterator().next().getId());
    }


    @Test
    public void deleteTest() {
        assertNotNull(em.find(Question.class, 31L));

        Survey s = repository.getOne(30L);

        repository.delete(s);
        repository.flush();

        assertNull(em.find(Survey.class, 30L));
        assertNull(em.find(Question.class, 31L));
    }

    @Test
    public void updateWithRemoveQuestionTest() {
        Survey s = repository.getOne(10L);
        s.setName("Updated");

        List<Question> copy = new ArrayList<>(s.getQuestions());
        copy.get(0).setQuestionText("Updated Question");
        copy.remove(1);

        s.setQuestions(copy);
        repository.saveAndFlush(s);
        em.clear();

        Optional<Survey> optionalSurvey = repository.findById(10L);
        assertTrue(optionalSurvey.isPresent());
        assertEquals("Updated", optionalSurvey.get().getName());
        assertEquals(2, optionalSurvey.get().getQuestions().size());

        // Check first question is updated
        Question q11 = em.find(Question.class, 11L);
        assertNotNull(q11);
        assertEquals("Updated Question", q11.getQuestionText());

        // Check second is removed
        assertNull(em.find(Question.class, 12L));
    }

    @Test
    public void addQuestionTest() {
        Survey survey = getSurveyWithNewQuestion();
        em.clear();
        Survey s = repository.findById(20L).get();
        int oldQuestionCount = s.getQuestions().size();

        s.updateFrom(survey);
        Survey savedSurvey = repository.saveAndFlush(s);

        assertEquals(oldQuestionCount + 1, savedSurvey.getQuestions().size());

    }

    private Survey getSurveyWithNewQuestion() {
        Optional<Survey> os = repository.findById(20L);
        assertTrue(os.isPresent());

        Survey s = os.get();

        Question q = new Question();
        q.setSurvey(s);
        q.setQuestionText("New Question");
        q.setQuestionOrder(Integer.MAX_VALUE);

        s.getQuestions().add(q);
        return s;
    }

    @ParameterizedTest
    @ValueSource(longs = {10, 20, 30})
    public void testSurveyByIdTest(long surveyId) {
        Optional<Survey> optionalSurvey = repository.findById(surveyId);
        assertTrue(optionalSurvey.isPresent());

        Survey s = optionalSurvey.get();
        assertEquals(surveyId, s.getId());

        assertNotNull(s.getQuestions());
        assertFalse(s.getQuestions().isEmpty());
        assertEquals(surveyId + 1, s.getQuestions().iterator().next().getId());
    }

    @Test
    public void testFindByExample() {
        ExampleMatcher customMatcher = ExampleMatcher.matchingAll()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Survey exampleSurvey = new Survey();
        exampleSurvey.setName("survey");
        exampleSurvey.setActive(Boolean.FALSE);
        exampleSurvey.setStartDate(LocalDate.parse("2020-01-30"));

        Example<Survey> example = Example.of(exampleSurvey, customMatcher);

        Optional<Survey> found = repository.findOne(example);
        assertTrue(found.isPresent());

        Survey foundSurvey = found.get();
        assertTrue(foundSurvey.getId().equals(20L));
    }


    @Test
    public void testFindWithPaginationAndSortByName() {
        Page<Survey> page = repository.findAll(PageRequest.of(1, 2, Sort.by("name")));
        assertFalse(page.isEmpty());
        Survey survey = page.iterator().next();
        assertEquals(survey.getId(), 30L);
    }

    @Test()
    public void testFindWithBrokenPagination() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.findAll(PageRequest.of(1, 0, Sort.by("name")));
        });
    }

    @TestConfiguration
    public static class TestConfig {
        @MockBean
        public DocumentationPluginsBootstrapper documentationPluginsBootstrapper;
    }
}
