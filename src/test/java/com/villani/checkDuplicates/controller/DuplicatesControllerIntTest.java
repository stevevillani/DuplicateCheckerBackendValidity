package com.villani.checkDuplicates.controller;

import com.villani.checkDuplicates.domain.PersonWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DuplicatesControllerIntTest {


    @Autowired
    DuplicatesController duplicatesController;

    MultipartFile multipartFile;

    MultipartFile invalidMultipartFile;


    @BeforeEach
    public void setUp() throws Exception{
        multipartFile = new MockMultipartFile("normal.csv", "normal.csv", "text/plain", new FileInputStream(new File("./normal.csv")));
    }


    @Test
    public void upload_success(){

        ResponseEntity result = duplicatesController.upload(multipartFile);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(90, ((PersonWrapper) result.getBody()).getUniques().size());
        assertEquals(8, ((PersonWrapper) result.getBody()).getDuplicates().size());

    }

}
