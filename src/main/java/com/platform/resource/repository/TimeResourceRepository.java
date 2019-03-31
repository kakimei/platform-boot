package com.platform.resource.repository;

import com.platform.resource.repository.entity.MetaType;
import com.platform.resource.repository.entity.TimeResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TimeResourceRepository extends JpaRepository<TimeResource, Long> {
    TimeResource findFirstByMetaTypeAndActiveIsTrueOrderByReservableDateDesc(MetaType metaType);

    List<TimeResource> findByMetaTypeAndRemainTimesGreaterThanAndActiveIsTrueOrderByReservableDateAscHourBeginAsc(MetaType metaType, Integer remainTimes);

    TimeResource findByMetaTypeAndReservableDateAndHourBeginAndMinuteBeginAndAndHourEndAndMinuteEndAndRemainTimesGreaterThanAndActiveIsTrue(MetaType metaType, Date reservableDate, Integer hourBegin, Integer minuteBegin, Integer hourEnd, Integer minuteEnd, Integer remainTimes);

    List<TimeResource> findByMetaTypeAndReservableDateBetweenAndActiveIsTrue(MetaType metaType, Date beginDate, Date endDate);

    List<TimeResource> findByMetaTypeAndReservableDateAndRemainTimesGreaterThanAndActiveIsTrue(MetaType metaType, Date reservableDate, Integer remainTimes);
}
