package com.platform.resource.repository;

import com.platform.resource.repository.entity.MetaType;
import com.platform.resource.repository.entity.TimeResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TimeResourceRepository extends JpaRepository<TimeResource, Long> {
    TimeResource findFirstByMetaTypeOrderByReservableDateDesc(MetaType metaType);

    List<TimeResource> findByMetaTypeAndRemainTimesGreaterThanOrderByReservableDateAscHourBeginAsc(MetaType metaType, Integer remainTimes);

    TimeResource findByMetaTypeAndReservableDateAndHourBeginAndMinuteBeginAndAndHourEndAndMinuteEndAndRemainTimesGreaterThan(MetaType metaType, Date reservableDate, Integer hourBegin, Integer minuteBegin, Integer hourEnd, Integer minuteEnd, Integer remainTimes);

    List<TimeResource> findByMetaTypeAndReservableDateBetween(MetaType metaType, Date beginDate, Date endDate);

    List<TimeResource> findByMetaTypeAndReservableDateAndRemainTimesGreaterThan(MetaType metaType, Date reservableDate, Integer remainTimes);
}
