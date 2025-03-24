package com.citc.nce.auth.serialnumber;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author jcrenc
 * @since 2024/5/23 15:07
 */
@SpringBootTest
public class GlobalSerialNumberServiceTest {
    @Autowired
    private GlobalSerialNumberService globalSerialNumberService;

    @Test
    public void applyGlobalUniqueSerialNumber() {
        globalSerialNumberService.applyGlobalUniqueSerialNumber(1,"111");
        globalSerialNumberService.applyGlobalUniqueSerialNumber(1,"111");
    }
}