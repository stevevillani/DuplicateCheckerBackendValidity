package com.villani.checkDuplicates.controller;

import com.villani.checkDuplicates.domain.PersonWrapper;
import com.villani.checkDuplicates.service.DuplicatesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DuplicatesControllerUnitTest {

    @Mock
    DuplicatesService mockDuplicatesService;

    @Mock
    MultipartFile mockMultipartFile;

    PersonWrapper personWrapper;

    @InjectMocks
    DuplicatesController duplicatesController;

    @BeforeEach
    public void setUp(){
        personWrapper = new PersonWrapper();

    }

    @Test
    public void upload_success() throws Exception{
        when(mockDuplicatesService.checkDuplicates(mockMultipartFile)).thenReturn(personWrapper);

        ResponseEntity result = duplicatesController.upload(mockMultipartFile);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(mockDuplicatesService).checkDuplicates(mockMultipartFile);
    }

    @Test
    public void upload_fail() throws Exception{
        when(mockDuplicatesService.checkDuplicates(mockMultipartFile)).thenThrow(new Exception());

        ResponseEntity result = duplicatesController.upload(mockMultipartFile);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());

        verify(mockDuplicatesService).checkDuplicates(mockMultipartFile);
    }
}
