package com.drp.service;

import com.drp.entity.ActivityAction;
import com.drp.entity.ActivityLog;
import com.drp.entity.User;
import com.drp.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public void log(User user, ActivityAction action, String entityType, Long entityId, String details) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<ActivityLog> getAll(Pageable pageable) {
        return activityLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}
