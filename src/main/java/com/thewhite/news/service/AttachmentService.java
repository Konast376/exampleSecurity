package com.thewhite.news.service;

import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.NewsRecord;
import com.whitesoft.core.services.ReferableEntityService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * Сервис для работы с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public interface AttachmentService extends ReferableEntityService<Attachment> {
    /**
     * Создание вложения
     *
     * @param name       имя файла вложения
     * @param newsRecord новость к которой крепится вложение
     */
    Attachment create(String name,
                      NewsRecord newsRecord,
                      InputStream inputStream);

    /**
     * Скачивание вложения
     *
     * @param id           uuid вложения
     * @param outputStream стрим куда будет отправлен скачиваемый файл
     */
    void download(UUID id, OutputStream outputStream);

    /**
     * Получить список вложений по новости
     *
     * @param newsRecordId uuid новости
     */
    List<Attachment> getAll(UUID newsRecordId);

    /**
     * Удаление вложения
     *
     * @param id uuid удаляемого вложения
     */
    void delete(UUID id);
}
