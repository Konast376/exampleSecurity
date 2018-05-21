package com.thewhite.news.service;

import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.model.RecordStatus;
import com.whitesoft.core.services.ReferableEntityService;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Итерфейс сервиса для работы с новостями
 */
public interface NewsRecordService extends ReferableEntityService<NewsRecord> {
    /**
     * Создание записи
     *
     * @param title   тема новости
     * @param content содержание
     * @param endDate дата актуальности
     * @param userId  uuid пользователя
     * @return
     */
    NewsRecord create(
            String title,
            Date postDate,
            String content,
            Date endDate,
            UUID userId
    );

    /**
     * Получение списка новостей
     *
     * @param userId        пользователья который
     * @param excludeUserId исключить новости прочитанные данным пользователем
     * @param recordStatus  статус новости
     * @param deadline      дата для получения актульны новостей с датой актуальности позже указанной
     * @param year          год новости
     * @param pageSize      размер страницы результатов
     * @param pageNo        номер страницы результатов
     * @return
     */
    Page<NewsRecord> getAll(UUID userId,
                            UUID excludeUserId,
                            RecordStatus recordStatus,
                            Date deadline,
                            Integer year,
                            int pageSize,
                            int pageNo);

    /**
     * Получить годы в которые были новости
     *
     * @return
     */
    List<Integer> getYears();

    /**
     * Редактирование существующей новости
     *
     * @param id      UUID редактируемой записи
     * @param title   тема новости
     * @param content содержание новости
     * @param endDate дата актуальности новости
     * @return
     */
    NewsRecord update(UUID id,
                      String title,
                      String content,
                      Date endDate);

    /**
     * Публикация новости
     *
     * @param id UUID публикуемой новости
     * @return
     */
    NewsRecord publish(UUID id);

    /**
     * Пометить новость как прочитанную
     *
     * @param id     UUID новости
     * @param userId uuid пользователя для которого ставится отметка
     * @return
     */
    NewsRecord mark(UUID id, UUID userId);

    /**
     * Удаление новости
     *
     * @param id uuid удаляемой записи
     */
    void delete(UUID id);
}
