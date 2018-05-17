package com.thewhite.news.api.mappers;

import com.thewhite.news.api.dto.AttachmentDTO;
import com.thewhite.news.model.Attachment;
import com.whitesoft.api.mappers.BaseMapper;
import org.mapstruct.Mapper;

/**
 * Created by Vdovin S. on 17.05.18.
 * Маппер для вложений
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment, AttachmentDTO> {
}
