package com.thewhite.news.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Vdovin S. on 16.05.2018.
 * Информация о залогиненом пользователи
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhoAmiDTO {
    @ApiModelProperty("Id Текущего пользователя")
    private UUID userId;
    @ApiModelProperty("Имя текущего пользователя")
    private String userFirstName;
    @ApiModelProperty("Отчество текущего пользователя")
    private String userMiddleName;
    @ApiModelProperty("Фамилия текущего пользователя")
    private String userLastName;
    @ApiModelProperty("имя учетной записи текущего пользователя")
    private String userLogin;
    @ApiModelProperty("Название должности сотрудника по умолчанию для текущего пользователя")
    private String postName;
    @ApiModelProperty("Роли текущего пользователя")
    private Set<String> authorities;
}
