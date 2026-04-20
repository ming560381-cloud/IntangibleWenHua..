package com.feiyi.feiyiwenhua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.entity.Activity;
import com.feiyi.feiyiwenhua.entity.ActivityRegistration;
import com.feiyi.feiyiwenhua.repository.ActivityRegistrationRepository;
import com.feiyi.feiyiwenhua.repository.ActivityRepository;
import com.feiyi.feiyiwenhua.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityRegistrationRepository activityRegistrationRepository;

    @Override
    public List<Activity> findAll() {
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Activity::getStartTime);
        return activityRepository.selectList(queryWrapper);
    }

    @Override
    public List<Activity> findPublished() {
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getStatus, 1).orderByAsc(Activity::getStartTime);
        return activityRepository.selectList(queryWrapper);
    }

    @Override
    public Activity findById(Long id) {
        return activityRepository.selectById(id);
    }

    @Override
    public Activity save(Activity activity) {
        Date now = new Date();
        if (activity.getId() == null) {
            activity.setCreateTime(now);
            activity.setUpdateTime(now);
            if (activity.getSignupCount() == null) {
                activity.setSignupCount(0);
            }
            if (activity.getStatus() == null) {
                activity.setStatus(1);
            }
            activityRepository.insert(activity);
        } else {
            activity.setUpdateTime(now);
            activityRepository.updateById(activity);
        }
        return activity;
    }

    @Override
    public void deleteById(Long id) {
        activityRepository.deleteById(id);
    }

    @Override
    public ActivityRegistration register(Long activityId, ActivityRegistration registration) {
        Activity activity = activityRepository.selectById(activityId);
        Date now = new Date();
        if (activity == null || activity.getStatus() == null || activity.getStatus() != 1) {
            throw new RuntimeException("活动不存在或未开放报名");
        }
        if (activity.getStartTime() != null && !isFutureDay(activity.getStartTime(), now)) {
            throw new RuntimeException("该活动报名已截止，仅可查看详情");
        }
        if (activity.getEndTime() != null && now.after(activity.getEndTime())) {
            throw new RuntimeException("该活动已结束，无法报名");
        }

        if (registration.getUserId() == null) {
            throw new RuntimeException("请先登录后再报名");
        }
        if (registration.getPhone() == null || registration.getPhone().trim().isEmpty()) {
            throw new RuntimeException("请填写手机号后再报名");
        }

        // 同一活动内，手机号或用户ID重复都视为重复报名
        LambdaQueryWrapper<ActivityRegistration> duplicateByPhone = new LambdaQueryWrapper<>();
        duplicateByPhone.eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getPhone, registration.getPhone().trim())
                .eq(ActivityRegistration::getStatus, 1);
        Long phoneCount = activityRegistrationRepository.selectCount(duplicateByPhone);
        if (phoneCount != null && phoneCount > 0) {
            throw new RuntimeException("该手机号已报名此活动，请勿重复报名");
        }

        LambdaQueryWrapper<ActivityRegistration> duplicateByUser = new LambdaQueryWrapper<>();
        duplicateByUser.eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, registration.getUserId())
                .eq(ActivityRegistration::getStatus, 1);
        Long userCount = activityRegistrationRepository.selectCount(duplicateByUser);
        if (userCount != null && userCount > 0) {
            throw new RuntimeException("您已报名过此活动，请勿重复报名");
        }

        int currentSignupCount = activity.getSignupCount() == null ? 0 : activity.getSignupCount();
        if (activity.getCapacity() != null && currentSignupCount >= activity.getCapacity()) {
            throw new RuntimeException("报名人数已满");
        }

        registration.setActivityId(activityId);
        registration.setPhone(registration.getPhone().trim());
        registration.setStatus(registration.getStatus() == null ? 1 : registration.getStatus());
        registration.setCreateTime(now);
        registration.setUpdateTime(now);
        activityRegistrationRepository.insert(registration);

        activity.setSignupCount(currentSignupCount + 1);
        activity.setUpdateTime(now);
        activityRepository.updateById(activity);

        return registration;
    }

    @Override
    public void cancelRegistration(Long registrationId, Long userId) {
        ActivityRegistration registration = activityRegistrationRepository.selectById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (registration.getUserId() == null || !registration.getUserId().equals(userId)) {
            throw new RuntimeException("无权取消该报名记录");
        }
        if (registration.getStatus() == null || registration.getStatus() != 1) {
            throw new RuntimeException("该报名已取消，无需重复操作");
        }

        Date now = new Date();
        registration.setStatus(0);
        registration.setUpdateTime(now);
        activityRegistrationRepository.updateById(registration);

        Activity activity = activityRepository.selectById(registration.getActivityId());
        if (activity != null) {
            int current = activity.getSignupCount() == null ? 0 : activity.getSignupCount();
            activity.setSignupCount(Math.max(0, current - 1));
            activity.setUpdateTime(now);
            activityRepository.updateById(activity);
        }
    }

    @Override
    public List<ActivityRegistration> findRegistrationsByActivity(Long activityId) {
        LambdaQueryWrapper<ActivityRegistration> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityRegistration::getActivityId, activityId)
                .orderByDesc(ActivityRegistration::getCreateTime);
        return activityRegistrationRepository.selectList(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> findRegistrationsByUser(Long userId) {
        LambdaQueryWrapper<ActivityRegistration> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityRegistration::getUserId, userId)
                .orderByDesc(ActivityRegistration::getCreateTime);
        List<ActivityRegistration> registrations = activityRegistrationRepository.selectList(queryWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (ActivityRegistration registration : registrations) {
            Activity activity = activityRepository.selectById(registration.getActivityId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", registration.getId());
            map.put("activityId", registration.getActivityId());
            map.put("activityTitle", activity != null ? activity.getTitle() : "活动已删除");
            map.put("participantName", registration.getParticipantName());
            map.put("phone", registration.getPhone());
            map.put("message", registration.getMessage());
            map.put("status", registration.getStatus());
            map.put("statusText", registration.getStatus() == 1 ? "已报名" : "报名已取消");
            map.put("createTime", registration.getCreateTime());
            result.add(map);
        }
        return result;
    }

    private boolean isFutureDay(Date target, Date now) {
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(target);
        targetDate.set(Calendar.HOUR_OF_DAY, 0);
        targetDate.set(Calendar.MINUTE, 0);
        targetDate.set(Calendar.SECOND, 0);
        targetDate.set(Calendar.MILLISECOND, 0);

        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(now);
        nowDate.set(Calendar.HOUR_OF_DAY, 0);
        nowDate.set(Calendar.MINUTE, 0);
        nowDate.set(Calendar.SECOND, 0);
        nowDate.set(Calendar.MILLISECOND, 0);
        return targetDate.after(nowDate);
    }
}
