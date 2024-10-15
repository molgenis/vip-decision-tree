package org.molgenis.vcf.decisiontree.filter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WriterThreadInterruptedExceptionTest {

    @Test
    void getMessage() {
        assertEquals(
                "VCF writer was interrupted with message: I don't want to interrupt, but ...",
                new WriterThreadInterruptedException(
                        "I don't want to interrupt, but ...").getMessage());
    }
}