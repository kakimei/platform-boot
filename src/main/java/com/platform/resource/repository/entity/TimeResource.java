package com.platform.resource.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "time_resource")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TimeResource {
    @Id
    @Column(name = "time_resource_id", unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "meta_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MetaType metaType;

    @Column(name = "reservable_date", nullable = false)
    private Date reservableDate;

    @Column(name = "hour_begin", nullable = false)
    private Integer hourBegin;

    @Column(name = "minute_begin", nullable = false)
    private Integer minuteBegin;

    @Column(name = "hour_end", nullable = false)
    private Integer hourEnd;

    @Column(name = "minute_end", nullable = false)
    private Integer minuteEnd;

    @Column(name = "remain_times", nullable = false)
    private Integer remainTimes;
}
