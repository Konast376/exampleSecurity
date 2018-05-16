package com.thewhite.news.api.dto;

import com.thewhite.news.model.RecordStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Vdovin S. on 16.05.18.
 * <p>
 * TODO: replace on javadoc
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsRecordDTO {
    @ApiModelProperty("Тема новости")
    private String title;
    @ApiModelProperty("Дата создания новости")
    private Date postDate;
    @ApiModelProperty("Содержание новости")
    private String content;
    @ApiModelProperty("Дата актуальности новости")
    private Date endDate;
    @ApiModelProperty("ID пользователя создавшего новость")
    private UUID userId;
    @ApiModelProperty("Статус новости")
    private RecordStatus status;
    @ApiModelProperty("Была ли прочитана новость текущим пользователем")
    private boolean hidden;
}
