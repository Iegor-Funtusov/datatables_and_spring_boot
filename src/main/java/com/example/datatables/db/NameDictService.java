package com.example.datatables.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NameDictService {

    @Getter @Setter private String firstNamePath;
    @Getter @Setter private String lastNamePath;
    @Getter @Setter private String departmentNamePath;

    public NameDictService() {
        this.firstNamePath = "/firstName.txt";
        this.lastNamePath = "/lastName.txt";
        this.departmentNamePath = "/departmentName.txt";
    }

    @Bean
    public NameDict loadNameDict() throws IOException {
        return loadNameDict(true);
    }

    private NameDict loadNameDict(boolean toUpperCase) throws IOException {
        long startLoad = System.currentTimeMillis();
        NameDict d = new NameDict();
        readFile(d, true, toUpperCase);
        readFile(d, false, toUpperCase);
        readFile(d, toUpperCase);
        log.info("Loaded " + d + " in " + (System.currentTimeMillis() - startLoad) + " ms");
        return d;
    }

    private void readFile(NameDict d, boolean firstName, boolean toUpperCase) throws IOException {
        String path;
        path = firstName ? firstNamePath : lastNamePath;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(path)))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (toUpperCase) {
                    d.add(line.toUpperCase(), firstName);
                } else {
                    d.add(line, firstName);
                }
            }
        }
    }

    private void readFile(NameDict d, boolean toUpperCase) throws IOException {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(departmentNamePath)))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (toUpperCase) {
                    d.addDep(line.toUpperCase());
                } else {
                    d.addDep(line);
                }
            }
        }
    }
}
