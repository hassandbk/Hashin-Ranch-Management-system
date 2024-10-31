package com.example.hashinfarm.utils;

import com.example.hashinfarm.data.DTOs.records.Country;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataLoader {
    public static List<Country> loadCountries() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File("src/main/resources/json/countriesFlagsAndCodes.json"),
                new TypeReference<>() {
                });
    }
}