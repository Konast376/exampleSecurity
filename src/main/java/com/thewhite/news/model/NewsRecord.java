package com.thewhite.news.model;


import com.whitesoft.core.data.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity(name = "news_record")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NewsRecord extends BaseEntity {
    @Column(length = 500)
    private String title;

    @Temporal(TemporalType.DATE)
    @Column(name = "post_date")
    private Date postDate;

    @Column(length = 5000)
    private String content;

    @Enumerated(EnumType.STRING)
    private RecordStatus status;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Type(type = "uuid-char")
    private UUID userId;

    @CollectionTable(name = "users_showed")
    @Type(type = "uuid-char")
    private Set<UUID> users;
}
