package com.drp.controller;

import com.drp.dto.response.ActivityLogResponse;
import com.drp.dto.response.ApiResponse;
import com.drp.dto.response.PageResponse;
import com.drp.service.ActivityLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ActivityLogResponse>> getActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ActivityLogResponse> response = PageResponse.from(
                activityLogService.getAll(pageable).map(ActivityLogResponse::from));
        return ApiResponse.success(response);
    }
}
