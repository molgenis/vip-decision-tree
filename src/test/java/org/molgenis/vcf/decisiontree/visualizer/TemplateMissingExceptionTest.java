package org.molgenis.vcf.decisiontree.visualizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TemplateMissingExceptionTest {
    @Test
    void getMessage() {
        assertEquals("Could not load the template file.",
                new TemplateMissingException().getMessage());
    }
}