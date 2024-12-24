package com.example.backspark.service;

import com.example.backspark.model.ISocks;
import com.example.backspark.model.SocksTransaction;
import com.example.backspark.repository.SocksRepository;
import com.example.backspark.util.CSVHelper;
import com.example.backspark.util.ComparisonOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocksService {

    private final SocksRepository socksRepository;

    @Transactional(readOnly = true)
    public int countAll(String color, ComparisonOperator operator, Integer cottonPart) {
        if (operator == ComparisonOperator.EQUAL)
            return socksRepository.countByColorAndCottonPart(color, cottonPart).orElse(0);
        if (operator == ComparisonOperator.LESS_THAN)
            return socksRepository.countByColorAndCottonPart(color, 0, cottonPart - 1).orElse(0);
        if (operator == ComparisonOperator.MORE_THAN)
            return socksRepository.countByColorAndCottonPart(color, cottonPart + 1, 100).orElse(0);
        throw new IllegalArgumentException();
    }

    @Transactional
    public SocksTransaction doIncome(String color, Integer cottonPart, Integer quantity) {
        var transaction = new SocksTransaction(null, color, cottonPart, quantity);
        return socksRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SocksTransaction doOutcome(String color, Integer cottonPart, Integer quantity) {
        var currentQuantity = socksRepository.countByColorAndCottonPart(color, cottonPart, cottonPart).orElse(0);

        if (currentQuantity + quantity < 0) throw new RuntimeException("Not enough socks in stock");

        var transaction = new SocksTransaction(null, color, cottonPart, quantity);
        return socksRepository.save(transaction);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SocksTransaction update(Long id, String color, Integer cottonPart, Integer quantity) {
        var transaction = socksRepository.findById(id).orElseThrow();

        var socksCount1 = socksRepository.countByColorAndCottonPart(transaction.getColor(), transaction.getCottonPart()).orElse(0);
        var socksCount2 = socksRepository.countByColorAndCottonPart(color, cottonPart).orElse(0);

        if (transaction.getColor().equals(color) && transaction.getCottonPart().equals(cottonPart)) {
            socksCount1 = socksCount1 + quantity - transaction.getQuantity();
        } else {
            socksCount1 -= transaction.getQuantity();
            socksCount2 += quantity;
        }

        if (socksCount1 < 0 || socksCount2 < 0) throw new RuntimeException("Not enough socks in stock");

        transaction.setColor(color);
        transaction.setCottonPart(cottonPart);
        transaction.setQuantity(quantity);
        return socksRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<ISocks> findAll(Integer minCottonPart, Integer maxCottonPart, String sortBy, Sort.Direction sortDirection) {
        return socksRepository.findAll(minCottonPart, maxCottonPart, Sort.by(sortDirection, sortBy));
    }

    @Transactional
    public void batchInsert(MultipartFile file) {
        try {
            if (!CSVHelper.hasCSVFormat(file)) throw new RuntimeException("Only CSV format supported");
            var socks = CSVHelper.csvToSocks(file.getInputStream());
            socksRepository.saveAll(socks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
