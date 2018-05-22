package com.thewhite.news.repositories;

import com.thewhite.news.model.Attachment;
import com.whitesoft.core.repositories.BaseRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by Vdovin S. on 16.05.18.
 * Репозиторий для вложений
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public interface AttachmentRepository extends BaseRepository<Attachment> {
    List<Attachment> findAllByNewsRecordId(UUID id);
}