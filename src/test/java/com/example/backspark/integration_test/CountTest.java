package com.example.backspark.integration_test;

import com.example.backspark.Util;
import com.example.backspark.model.SocksTransaction;
import com.example.backspark.service.SocksService;
import com.example.backspark.util.ComparisonOperator;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CountTest {

    private final SocksService socksService;
    private final EntityManager em;

    @ParameterizedTest
    @MethodSource("countArguments")
    public void testCountAllWithEqualsOperator(String color, int cottonPart, List<SocksTransaction> data) {
        for (var d : data) Util.insertToDbNative(em, d);

        var expected = data.stream()
                .filter(st -> st.getCottonPart() == cottonPart)
                .filter(st -> st.getColor().equals(color))
                .mapToInt(SocksTransaction::getQuantity)
                .sum();
        var actual = socksService.countAll(color, ComparisonOperator.EQUAL, cottonPart);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("countArguments")
    public void testCountAllWithLessOperator(String color, int cottonPart, List<SocksTransaction> data) {
        for (var d : data) Util.insertToDbNative(em, d);

        var expected = data.stream()
                .filter(st -> st.getCottonPart() < cottonPart)
                .filter(st -> st.getColor().equals(color))
                .mapToInt(SocksTransaction::getQuantity)
                .sum();
        var actual = socksService.countAll(color, ComparisonOperator.LESS_THAN, cottonPart);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("countArguments")
    public void testCountAllWithMoreOperator(String color, int cottonPart, List<SocksTransaction> data) {
        for (var d : data) Util.insertToDbNative(em, d);

        var expected = data.stream()
                .filter(st -> st.getCottonPart() > cottonPart)
                .filter(st -> st.getColor().equals(color))
                .mapToInt(SocksTransaction::getQuantity)
                .sum();
        var actual = socksService.countAll(color, ComparisonOperator.MORE_THAN, cottonPart);

        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> countArguments() {
        return Stream.of(
                arguments("red", 1, getSocksTransactions()),
                arguments("red", 50, getSocksTransactions()),
                arguments("green", 1, getSocksTransactions()),
                arguments("green", 100, getSocksTransactions()),
                arguments("green", 0, getSocksTransactions())
        );
    }

    private static List<SocksTransaction> getSocksTransactions() {
        return List.of(
                new SocksTransaction(1L, "red", 1, 1),
                new SocksTransaction(2L, "red", 1, 2),
                new SocksTransaction(3L, "red", 1, -1),
                new SocksTransaction(4L, "red", 50, 5),
                new SocksTransaction(5L, "green", 1, 5),
                new SocksTransaction(6L, "green", 50, 5)
        );
    }
}