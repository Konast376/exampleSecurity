package com.thewhite.news.api.mappers;

import com.thewhite.news.api.dto.NewsRecordDTO;
import com.thewhite.news.model.NewsRecord;
import com.whitesoft.api.mappers.BaseMapper;
import org.mapstruct.Mapper;

/**
 * Created by Vdovin S. on 17.05.18.
 * Маппер для новостей
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Mapper
public interface NewsRecordMapper extends BaseMapper<NewsRecord, NewsRecordDTO> {
}
