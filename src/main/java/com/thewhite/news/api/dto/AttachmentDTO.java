package com.thewhite.news.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Vdovin S. on 16.05.18.
 * ДТО для вложений
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {
    @ApiModelProperty("id вложения")
    private UUID id;
    @ApiModelProperty("имя файла")
    private String name;
    @ApiModelProperty("Id новости к которой привязано вложение")
    private UUID newsRecordId;
}
