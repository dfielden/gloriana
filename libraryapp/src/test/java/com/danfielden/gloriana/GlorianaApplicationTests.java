package com.danfielden.gloriana;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"goat=/tmp/gloriana_test.db"})
class GlorianaApplicationTests {

    @Value("goat")
    String databasePath;

    @Test
    void contextLoads() {
        // This test is empty, and just makes sure the application can boot with the default settings.
    }

}
