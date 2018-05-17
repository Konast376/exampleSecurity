package com.thewhite.news.api;

import com.thewhite.news.actions.arguments.CreateAttachmentActionArgument;
import com.thewhite.news.api.dto.AttachmentDTO;
import com.thewhite.news.api.dto.NewsCreateDTO;
import com.thewhite.news.api.dto.NewsRecordDTO;
import com.thewhite.news.api.dto.NewsUpdateDTO;
import com.thewhite.news.api.mappers.AttachmentMapper;
import com.thewhite.news.api.mappers.NewsRecordMapper;
import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.RecordStatus;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.AuthService;
import com.thewhite.news.service.NewsRecordService;
import com.whitesoft.api.dto.CollectionDTO;
import com.whitesoft.util.actions.Action;
import com.whitesoft.util.actions.OneFieldActionArgument;
import com.whitesoft.util.exceptions.WSInternalException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * Контроллер новостей
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@RestController
@RequestMapping(NewsRecordController.URI)
public class NewsRecordController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(NewsRecordController.class);
    public final static String URI = "/news";

    private final NewsRecordService newsRecordService;
    private final AttachmentService attachmentService;
    private final NewsRecordMapper newsRecordMapper;
    private final AttachmentMapper attachmentMapper;
    private final AuthService authService;
    private final Action<Attachment> createAttachmentAction;
    private final Action<Void> deleteNewsRecordAction;

    @Autowired
    public NewsRecordController(NewsRecordService newsRecordService,
                                AttachmentService attachmentService,
                                NewsRecordMapper newsRecordMapper,
                                AuthService authService,
                                Action<Attachment> createAttachmentAction,
                                AttachmentMapper attachmentMapper,
                                Action<Void> deleteNewsRecordAction) {
        this.newsRecordService = newsRecordService;
        this.attachmentService = attachmentService;
        this.newsRecordMapper = newsRecordMapper;
        this.authService = authService;
        this.createAttachmentAction = createAttachmentAction;
        this.attachmentMapper = attachmentMapper;
        this.deleteNewsRecordAction = deleteNewsRecordAction;
    }

    /**
     * POST /news/create - Создание новости
     *
     * @param createDTO данные для создания
     * @return созданная запись
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public NewsRecordDTO create(@RequestBody NewsCreateDTO createDTO) {
        return newsRecordMapper.toDTO(newsRecordService.create(
                createDTO.getTitle(),
                new Date(),
                createDTO.getContent(),
                createDTO.getEndDate(),
                authService.getCurrentUserId()));
    }

    /**
     * GET /news/{id} - получение новости по ID
     *
     * @param id uuid новости
     */
    @GetMapping("/{id}")
    public NewsRecordDTO getOne(@PathVariable UUID id) {
        return newsRecordMapper.toDTO(
                newsRecordService.getExisting(id));
    }

    /**
     * GET /news/actual/list - получить актуальные для данного пользователя новости
     *
     * @param pageSize размер страницы результатов
     * @param pageNo   номер страницы результатов
     * @return постраничный список новостей
     */
    @GetMapping("/actual/list")
    public CollectionDTO<NewsRecordDTO> getActualNews(@RequestParam int pageSize,
                                                      @RequestParam int pageNo) {
        return newsRecordMapper.getPageMapper()
                               .apply(newsRecordService.getAll(
                                       authService.getCurrentUserId(),
                                       RecordStatus.PUBLISHED,
                                       null,
                                       pageSize,
                                       pageNo));
    }

    /**
     * GET /news/list - получить новости
     *
     * @param recordStatus статус нововсти
     * @param year         год новости
     * @param pageSize     размер страницы результатов
     * @param pageNo       номер страницы результатов
     */
    @GetMapping("/list")
    public CollectionDTO<NewsRecordDTO> getAll(@RequestParam RecordStatus recordStatus,
                                               @RequestParam Integer year,
                                               @RequestParam int pageSize,
                                               @RequestParam int pageNo) {
        return newsRecordMapper.getPageMapper()
                               .apply(newsRecordService.getAll(null,
                                                               recordStatus,
                                                               year,
                                                               pageSize,
                                                               pageNo));
    }

    /**
     * GET /news/years - получить список годов, в которых есть новости
     */
    @GetMapping("/years")
    public CollectionDTO<Integer> getYears() {
        return new CollectionDTO<>(newsRecordService.getYears());
    }

    /**
     * POST /news/{id}/update - редактирование новости
     *
     * @param id        uuid новости
     * @param updateDTO данные для обновления
     */
    @PostMapping("/{id}/update")
    public NewsRecordDTO update(@PathVariable UUID id,
                                @RequestBody NewsUpdateDTO updateDTO) {
        return newsRecordMapper.toDTO(
                newsRecordService.update(id,
                                         updateDTO.getTitle(),
                                         updateDTO.getContent(),
                                         updateDTO.getEndDate()));
    }

    /**
     * POST /news/{id}/publish Публикация новости
     *
     * @param id uuid публикуемой новости
     */
    @PostMapping("/{id}/publish")
    public NewsRecordDTO publish(@PathVariable UUID id) {
        return newsRecordMapper.toDTO(newsRecordService.publish(id));
    }

    /**
     * POST /news/{id}/mark - пометить новость как прочитанную
     *
     * @param id uuid новости
     */
    @PostMapping("/{id}/mark")
    public NewsRecordDTO mark(@PathVariable UUID id) {
        return newsRecordMapper.toDTO(newsRecordService.mark(id));
    }

    /**
     * POST /news/{id}/delete - удалить новость
     *
     * @param id uuid удаляемой новости
     */
    @PostMapping("/{id}/delete")
    public void delete(@PathVariable UUID id) {
        deleteNewsRecordAction.execute(OneFieldActionArgument.of(id));
    }

    /**
     * POST /news/{id}/attachments/create - загрузка вложения
     *
     * @param id   uuid новости
     * @param data файл вложения
     */
    @PostMapping("/{id}/attachments/create")
    public AttachmentDTO attach(@PathVariable UUID id,
                                @RequestPart("data") MultipartFile data) {
        try {
            try (InputStream inputStream = data.getInputStream()) {
                return attachmentMapper.toDTO(
                        createAttachmentAction.execute(
                                CreateAttachmentActionArgument.builder()
                                                              .newsRecord(id)
                                                              .name(data.getOriginalFilename())
                                                              .fileData(inputStream)
                                                              .build()));
            }
        } catch (IOException ex) {
            logger.error("File upload failed with:", ex);
            throw new WSInternalException("Ошибка загрузки файла", -1);
        }
    }

    /**
     * GET /news/{id}/attachments/list - получить список вложений
     *
     * @param id uuid новости
     */
    @GetMapping("/{id}/attachments/list")
    public CollectionDTO<AttachmentDTO> getAttachments(@PathVariable UUID id) {
        return attachmentMapper.getCollectionMapper()
                               .apply(attachmentService.getAll(id));
    }
}
