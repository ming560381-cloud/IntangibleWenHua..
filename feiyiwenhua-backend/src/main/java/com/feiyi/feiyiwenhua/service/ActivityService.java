package com.feiyi.feiyiwenhua.service;

import com.feiyi.feiyiwenhua.entity.Activity;
import com.feiyi.feiyiwenhua.entity.ActivityRegistration;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    List<Activity> findAll();

    List<Activity> findPublished();

    Activity findById(Long id);

    Activity save(Activity activity);

    void deleteById(Long id);

    ActivityRegistration register(Long activityId, ActivityRegistration registration);

    void cancelRegistration(Long registrationId, Long userId);

    List<ActivityRegistration> findRegistrationsByActivity(Long activityId);

    List<Map<String, Object>> findRegistrationsByUser(Long userId);
}
