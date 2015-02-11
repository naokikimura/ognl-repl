package example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    static {
        System.setProperty("java.util.logging.config.class", LogConfig.class.getName());
    }

    public static class LogConfig {
        {
            Properties properties = new Properties();
            properties.setProperty("handlers", "java.util.logging.ConsoleHandler");
            properties.setProperty(".level", "ALL");
            properties.setProperty("java.util.logging.ConsoleHandler.level", "FINEST");
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                properties.store(out, null);
                InputStream in = new ByteArrayInputStream(out.toByteArray());
                LogManager.getLogManager().readConfiguration(in);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp() throws Exception {
        int n = 1;
        String expected = String.format("%d%n", n + n);
        String ognl = String.format("%d + %d%n", n ,n);
        ByteArrayInputStream in = new ByteArrayInputStream(ognl.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        App app = App.create(in, out, out);
        Map context = Collections.EMPTY_MAP;
        app.execute(context);
        String actual = new String(out.toByteArray());
        assertEquals(expected, actual);
    }
}
