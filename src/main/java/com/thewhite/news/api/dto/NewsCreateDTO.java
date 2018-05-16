package com.thewhite.news.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * Created by Vdovin S. on 16.05.18.
 * Дто для создания новости
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsCreateDTO {
    @ApiModelProperty("Тема новости")
    private String title;
    @ApiModelProperty("Содержание новости")
    private String content;
    @ApiModelProperty("Дата актуальности новости")
    private Date endDate;
}
