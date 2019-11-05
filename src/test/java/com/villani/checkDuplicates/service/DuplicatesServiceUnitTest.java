package com.villani.checkDuplicates.service;

import com.villani.checkDuplicates.domain.PersonWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DuplicatesServiceUnitTest {

    @Mock
    MultipartFile mockMultipartFile;

    MultipartFile multipartFile;

    @InjectMocks
    DuplicatesService duplicatesService;

    @BeforeEach
    public void setUp() throws Exception {
        duplicatesService = new DuplicatesService();
        multipartFile = new MockMultipartFile("normal.csv", "normal.csv", "text/plain", new FileInputStream(new File("./normal.csv")));

    }

    @Test
    public void upload_success() throws Exception {

        PersonWrapper personWrapper = duplicatesService.checkDuplicates(multipartFile);

        assertEquals(8, personWrapper.getDuplicates().size());
        assertEquals(90, personWrapper.getUniques().size());

    }

    @Test
    public void upload_fail() {
        when(mockMultipartFile.getOriginalFilename()).thenReturn("fail");

        assertThrows(NullPointerException.class, () -> {
            duplicatesService.checkDuplicates(mockMultipartFile);
        });

    }


}
