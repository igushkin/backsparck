package com.example.backspark.integration_test;

import com.example.backspark.Util;
import com.example.backspark.model.SocksTransaction;
import com.example.backspark.service.SocksService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FindTest {
    private final SocksService socksService;
    private final EntityManager em;

    @Test
    public void testFind() {
        Util.insertToDbNative(em, new SocksTransaction(1L, "red", 1, 5));
        Util.insertToDbNative(em, new SocksTransaction(2L, "blue", 2, 5));
        Util.insertToDbNative(em, new SocksTransaction(3L, "red", 3, 5));
        Util.insertToDbNative(em, new SocksTransaction(4L, "blue", 4, 5));

        Assertions.assertEquals(1,
                socksService.findAll(1, 1, "count", Sort.Direction.ASC).size());

        Assertions.assertEquals(4,
                socksService.findAll(1, 4, "count", Sort.Direction.ASC).size());

        Assertions.assertEquals(2,
                socksService.findAll(2, 3, "count", Sort.Direction.ASC).size());
    }
}
