package com.thewhite.security.model;

import com.whitesoft.core.data.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "diary")
public class Diary extends BaseEntity {

    @Column(name = "writer_id", nullable = false)
    private UUID writerId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "record")
    private String record;

    @Column(name = "record_date")
    private Date recordDate;
}