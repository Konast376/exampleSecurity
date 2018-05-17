package com.thewhite.news.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.repositories.AttachmentRepository;
import com.whitesoft.api.dto.CollectionDTO;
import com.whitesoft.cloud.feign.content.ContentDescriptor;
import com.whitesoft.util.TempFileHelper;
import com.whitesoft.util.exceptions.WSInternalException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * <p>
 * TODO: replace on javadoc
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
    public Attachment create(String name, NewsRecord newsRecord, InputStream inputStream) {
        return;
    }

    @Override
    public void download(UUID id, OutputStream outputStream) {

    }

    @Override
    public List<Attachment> getAll(UUID newsRecordId) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public Attachment getExisting(UUID id) {
        return null;
    }

    /**
     * Отправка файла в хранилище
     *
     * @throws Exception
     */
    private ContentDescriptor sendFileToStorage(InputStream inputStream) throws Exception {
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
                throw new WSInternalException("Fail", -1);
            }

        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }
}
