package com.platform.activity.repository;

import com.platform.activity.repository.entity.Activity;
import com.platform.activity.repository.entity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	List<Activity> findActivitiesByActivityType(ActivityType activityType);

	Activity findActivityByActivityId(Long activityId);
}
