package com.thewhite.news.model;

import com.whitesoft.core.data.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

/**
 * Created by Vdovin S. on 16.05.18.
 * Сущность вложения в новость
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment extends BaseEntity {

    private String name;

    @Column(name = "container_id")
    @Type(type = "uuid-char")
    private UUID containerId;

    @JoinColumn(name = "news_record_id")
    @ManyToOne
    private NewsRecord newsRecord;
}
