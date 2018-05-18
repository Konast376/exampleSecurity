package com.thewhite.news.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.repositories.AttachmentRepository;
import com.whitesoft.cloud.feign.content.ContentDescriptor;
import com.whitesoft.util.TempFileHelper;
import com.whitesoft.util.exceptions.WSInternalException;
import com.whitesoft.util.exceptions.WSNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.util.List;
import java.util.UUID;

import static com.thewhite.news.errorinfo.AttachmentErrorInfo.*;

/**
 * Created by Vdovin S. on 17.05.18.
 * Реализация сервиса для работы с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Component("attachmentService")
public class AttachmentServiceImpl implements AttachmentService {
    private Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final AttachmentRepository attachmentRepository;
    private final String contentStorageUri;

    @Autowired
    public AttachmentServiceImpl(RestTemplate restTemplate,
                                 AttachmentRepository attachmentRepository,
                                 @Value("content.storage.url") String contentStorageUri) {
        this.restTemplate = restTemplate;
        this.attachmentRepository = attachmentRepository;
        this.contentStorageUri = contentStorageUri;
    }

    @Override
    @Transactional
    public Attachment create(String name, NewsRecord newsRecord, InputStream inputStream) {

        ContentDescriptor descriptor = sendFileToStorage(inputStream);

        return attachmentRepository.save(Attachment.builder()
                                                   .containerId(descriptor.getNodeId())
                                                   .mimeType(descriptor.getMediaType())
                                                   .newsRecord(newsRecord)
                                                   .build());
    }

    @Override
    @Transactional(readOnly = true)
    public void download(UUID id, OutputStream outputStream) {
        Attachment attachment = getExisting(id);

        ResponseExtractor<Boolean> responseExtractor = response -> {
            try {
                IOUtils.copy(response.getBody(), outputStream);
                return true;
            } catch (IOException ex) {
                logger.error("File download failed with:", ex);
            }
            return false;
        };

        if (!restTemplate.execute(UriComponentsBuilder
                                          .fromHttpUrl("{url}/{id}/create")
                                          .buildAndExpand(contentStorageUri,
                                                          attachment.getContainerId())
                                          .toUri(),
                                  HttpMethod.GET,
                                  null,
                                  responseExtractor)) {
            throw new WSInternalException(FILE_DOWNLOAD_FAILED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attachment> getAll(UUID newsRecordId) {
        return attachmentRepository.findAllByNewsRecordId(newsRecordId);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(UUID id) {
        Attachment attachment = getExisting(id);
        try {
            ResponseEntity<Object> result =
                    restTemplate.postForEntity(UriComponentsBuilder
                                                       .fromUriString("{url}/{id}/delete")
                                                       .buildAndExpand(contentStorageUri,
                                                                       attachment.getContainerId())
                                                       .toUri(),
                                               null,
                                               Object.class);
            if (result.getStatusCode() != HttpStatus.OK) {
                logger.error("Can't delete file with: ", result.getBody());
                throw new WSInternalException(FILE_DELETE_FAILED);
            }
            attachmentRepository.delete(attachment);
        } catch (RestClientException ex) {
            logger.error("Can't delete file with: ", ex);
            throw new WSInternalException(FILE_DELETE_FAILED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Attachment getExisting(UUID id) {
        return attachmentRepository.findById(id)
                                   .orElseThrow(WSNotFoundException.of(ATTACHMENT_NOT_FOUND));
    }

    /**
     * Отправка файла в хранилище
     *
     * @throws Exception
     */
    private ContentDescriptor sendFileToStorage(InputStream inputStream) {
        try {
            File file = TempFileHelper.createTempFile(".pdf").toFile();
            try {
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    IOUtils.copy(inputStream, outputStream);
                }
                FileSystemResource fileSystemResource = new FileSystemResource(file);

                MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
                request.add("data", fileSystemResource);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(request, headers);

                ResponseEntity<String> requestResult = restTemplate.exchange(
                        UriComponentsBuilder.fromUriString("{url}/{endpoint}")
                                            .buildAndExpand(contentStorageUri, "/create")
                                            .toUri(),
                        HttpMethod.POST,
                        entity,
                        String.class);

                if (requestResult.getStatusCode() == HttpStatus.OK) {
                    ContentDescriptor results = objectMapper.readValue(requestResult.getBody(),
                                                                       ContentDescriptor.class);
                    logger.info(results.toString());
                    return results;
                } else {
                    logger.error("File upload failed with", requestResult.getBody());
                    throw new WSInternalException(FILE_UPLOAD_FAILED);
                }

            } finally {
                if (file != null) {
                    file.delete();
                }
            }
        } catch (IOException ex) {
            logger.error("File upload failed with:", ex);
            throw new WSInternalException(FILE_UPLOAD_FAILED);
        }
    }

}
