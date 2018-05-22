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
     * @param argument данные для создания
     * @return
     */
    NewsRecord create(
            CreateNewsRecordArgument argument
    );

    /**
     * Получение списка новостей
     *
     * @param userId       пользователья который
     * @param recordStatus статус новости
     * @param year         год новости
     * @param pageSize     размер страницы результатов
     * @param pageNo       номер страницы результатов
     * @return
     */
    Page<NewsRecord> getAll(UUID userId,
                            RecordStatus recordStatus,
                            Integer year,
                            int pageSize,
                            int pageNo);

    /**
     * Получить актуальные новости
     *
     * @param userId   uuid пользователя для которого нужно получить непрочитанные новости
     * @param deadline дата относительно которой проверяем актуальность
     * @param pageSize размер страницы результатов
     * @param pageNo   номер страницы результатов
     * @return
     */
    Page<NewsRecord> getActual(UUID userId,
                               Date deadline,
                               int pageSize,
                               int pageNo);

    /**
     * Получить годы в которые были новости
     *
     * @return
     */
    List<Integer> getYearsThatHasNews();

    /**
     * Редактирование существующей новости
     *
     * @param argument данные для обновления
     * @return
     */
    NewsRecord update(UpdateNewsRecordArgument argument);

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