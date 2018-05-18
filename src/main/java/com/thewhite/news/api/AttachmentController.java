package com.thewhite.news.api;

import com.thewhite.news.api.mappers.AttachmentMapper;
import com.thewhite.news.model.Attachment;
import com.thewhite.news.service.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * Контроллер для работы с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@RestController
@RequestMapping(AttachmentController.URI)
public class AttachmentController {
    Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    public static final String URI = "/attachments";

    private final AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    /**
     * GET /attachments/{id} - получить вложение
     *
     * @param id uuid вложения
     * @return
     */
    @GetMapping("/{id}")
    public void download(@PathVariable UUID id, HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            Attachment attachment = attachmentService.getExisting(id);

            response.setHeader("Content-Disposition", "attachment:filename=" +
                                                      URLEncoder.encode(attachment.getName(), "UTF-8"));
            response.setContentType(attachment.getMimeType());
            attachmentService.download(id, outputStream);
        } catch (IOException ex) {
            logger.error("Download file failed with:", ex);
        }
    }

    /**
     * POST /attachments/{id}/delete - удаление вложения
     *
     * @param id uuid удаляемого вложения
     */
    @PostMapping("/{id}/delete")
    public void delete(@PathVariable UUID id) {
        attachmentService.delete(id);
    }
}
