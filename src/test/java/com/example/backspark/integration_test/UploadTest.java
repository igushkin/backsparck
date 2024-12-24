package com.example.backspark.integration_test;

import com.example.backspark.model.SocksTransaction;
import com.example.backspark.service.SocksService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UploadTest {
    private final SocksService socksService;
    private final EntityManager em;

    @Test
    public void testValidUpload() throws IOException {
        var file = Files.createTempFile("data", ".csv");

        for (var i = 0; i < 10; i++)
            Files.writeString(file, "red,35,5" + System.lineSeparator(), StandardOpenOption.APPEND);

        var stream = new FileInputStream(file.toFile());
        var multipartFile = new MockMultipartFile("file", file.getFileName().toString(), MediaType.APPLICATION_OCTET_STREAM_VALUE, stream);

        this.socksService.batchInsert(multipartFile);
        var dbResult = em.createQuery("select st from SocksTransaction st", SocksTransaction.class).getResultList();

        Assertions.assertEquals(10, dbResult.size());
    }

    @Test
    public void testInvalidFileFormat() throws IOException {
        var file = Files.createTempFile("data", ".txt");

        var stream = new FileInputStream(file.toFile());
        var multipartFile = new MockMultipartFile("file", file.getFileName().toString(), MediaType.APPLICATION_OCTET_STREAM_VALUE, stream);

        Assertions.assertThrows(RuntimeException.class, () -> this.socksService.batchInsert(multipartFile));
    }
}
