package com.example.backspark.util;


import com.example.backspark.model.SocksTransaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {
    public static final String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<SocksTransaction> csvToSocks(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             var csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withRecordSeparator(System.lineSeparator()))) {

            var socks = new ArrayList<SocksTransaction>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                socks.add(SocksTransaction.builder()
                        .color(csvRecord.get(0))
                        .cottonPart(Integer.parseInt(csvRecord.get(1)))
                        .quantity(Integer.parseInt(csvRecord.get(2)))
                        .build());
            }
            return socks;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}