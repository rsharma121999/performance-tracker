package com.hr.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr.tracker.dto.request.CreateEmployeeRequest;
import com.hr.tracker.dto.request.CreateReviewRequest;
import com.hr.tracker.enums.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PerformanceTrackerApplicationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;



    @Test
    void createEmployee_valid_returns201WithEnumDepartment() throws Exception {
        var req = new CreateEmployeeRequest(
            "Test User", Department.QA, "Tester", LocalDate.of(2024, 1, 15));

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.department").value("QA"))
            .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createEmployee_blankName_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"","department":"ENGINEERING","role":"Dev","joiningDate":"2024-01-01"}
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void createEmployee_invalidDepartment_returns400WithEnumHint() throws Exception {
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Jane","department":"INVALID_DEPT","role":"Dev","joiningDate":"2024-01-01"}
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Allowed values")));
    }

    @Test
    void createEmployee_futureDateJoining_returns400() throws Exception {
        var req = new CreateEmployeeRequest(
            "Future", Department.HR, "Analyst", LocalDate.of(2099, 1, 1));

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]", containsString("future")));
    }

    @Test
    void createEmployee_missingFields_returns400WithMultipleErrors() throws Exception {
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details.length()", greaterThanOrEqualTo(3)));
    }



    @Test
    void submitReview_valid_returns201WithRatingLabel() throws Exception {
        var req = new CreateReviewRequest(1L, 1L, 2L, 4, "Good work");

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.rating").value(4))
            .andExpect(jsonPath("$.ratingLabel").value("Exceeds Expectations"))
            .andExpect(jsonPath("$.employeeId").value(1))
            .andExpect(jsonPath("$.reviewerId").value(2));
    }

    @Test
    void submitReview_selfReview_reviewerIdNull() throws Exception {
        var req = new CreateReviewRequest(3L, 1L, null, 3, "Self assessment");

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.reviewerId").isEmpty())
            .andExpect(jsonPath("$.reviewerName").isEmpty());
    }

    @Test
    void submitReview_ratingOutOfRange_returns400() throws Exception {
        var req = new CreateReviewRequest(1L, 1L, null, 6, "Too high");

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void submitReview_nonexistentEmployee_returns404() throws Exception {
        var req = new CreateReviewRequest(999L, 1L, null, 3, "Ghost");

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isNotFound());
    }

    @Test
    void submitReview_nonexistentCycle_returns404() throws Exception {
        var req = new CreateReviewRequest(1L, 999L, null, 3, "No cycle");

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isNotFound());
    }

    @Test
    void submitReview_notesTooLong_returns400() throws Exception {
        String longNotes = "x".repeat(4001);
        var req = new CreateReviewRequest(1L, 1L, null, 3, longNotes);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }



    @Test
    void getEmployeeReviews_returnsListWithCycleAndRatingLabel() throws Exception {
        mockMvc.perform(get("/employees/1/reviews"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].cycle.name").value("Q1 2025"))
            .andExpect(jsonPath("$[0].ratingLabel").isString());
    }

    @Test
    void getEmployeeReviews_nonexistent_returns404() throws Exception {
        mockMvc.perform(get("/employees/999/reviews"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getEmployeeReviews_invalidId_returns400() throws Exception {
        mockMvc.perform(get("/employees/abc/reviews"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Long")));
    }



    @Test
    void getCycleSummary_defaultStrategy() throws Exception {
        mockMvc.perform(get("/cycles/1/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cycleId").value(1))
            .andExpect(jsonPath("$.cycleName").value("Q1 2025"))
            .andExpect(jsonPath("$.ratingStrategy").value("AVERAGE"))
            .andExpect(jsonPath("$.averageRating").isNumber())
            .andExpect(jsonPath("$.topPerformer.employeeName").isString())
            .andExpect(jsonPath("$.goals.completed").value(5))
            .andExpect(jsonPath("$.goals.missed").value(2))
            .andExpect(jsonPath("$.goals.pending").value(2));
    }

    @Test
    void getCycleSummary_weightedRecentStrategy() throws Exception {
        mockMvc.perform(get("/cycles/1/summary").param("ratingStrategy", "WEIGHTED_RECENT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ratingStrategy").value("WEIGHTED_RECENT"))
            .andExpect(jsonPath("$.averageRating").isNumber());
    }

    @Test
    void getCycleSummary_medianStrategy() throws Exception {
        mockMvc.perform(get("/cycles/1/summary").param("ratingStrategy", "MEDIAN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ratingStrategy").value("MEDIAN"));
    }

    @Test
    void getCycleSummary_unknownStrategy_fallsBackToAverage() throws Exception {
        mockMvc.perform(get("/cycles/1/summary").param("ratingStrategy", "NONEXISTENT"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ratingStrategy").value("AVERAGE"));
    }

    @Test
    void getCycleSummary_nonexistent_returns404() throws Exception {
        mockMvc.perform(get("/cycles/999/summary"))
            .andExpect(status().isNotFound());
    }



    @Test
    void exportCycleSummary_json() throws Exception {
        mockMvc.perform(get("/cycles/1/export").param("format", "JSON"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(header().string("Content-Disposition", containsString(".json")));
    }

    @Test
    void exportCycleSummary_csv() throws Exception {
        mockMvc.perform(get("/cycles/1/export").param("format", "CSV"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/csv"));
    }

    @Test
    void exportCycleSummary_text() throws Exception {
        mockMvc.perform(get("/cycles/1/export").param("format", "TEXT"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void exportCycleSummary_unknownFormat_fallsBackToJson() throws Exception {
        mockMvc.perform(get("/cycles/1/export").param("format", "XLSX"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/json"));
    }

    @Test
    void exportCycleSummary_withStrategyAndFormat() throws Exception {
        mockMvc.perform(get("/cycles/1/export")
                .param("format", "CSV").param("ratingStrategy", "MEDIAN"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "text/csv"));
    }



    @Test
    void filterEmployees_byDepartmentEnum() throws Exception {
        mockMvc.perform(get("/employees")
                .param("department", "ENGINEERING").param("minRating", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].department").value("ENGINEERING"));
    }

    @Test
    void filterEmployees_invalidDepartment_returns400() throws Exception {
        mockMvc.perform(get("/employees")
                .param("department", "FAKE").param("minRating", "1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void filterEmployees_withMedianStrategy() throws Exception {
        mockMvc.perform(get("/employees")
                .param("minRating", "1").param("ratingStrategy", "MEDIAN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void filterEmployees_highMinRating() throws Exception {
        mockMvc.perform(get("/employees").param("minRating", "4.5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].averageRating", everyItem(greaterThanOrEqualTo(4.5))));
    }



    @Test
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{broken json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Malformed")));
    }
}
