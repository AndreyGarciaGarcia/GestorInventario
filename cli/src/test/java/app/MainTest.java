package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Comprobación mínima de que la clase de arranque existe y es accesible.
 */
class MainTest {

    @Test
    void mainPuedeIniciarse() {
        assertDoesNotThrow(() -> Class.forName("app.Main"));
    }
}
