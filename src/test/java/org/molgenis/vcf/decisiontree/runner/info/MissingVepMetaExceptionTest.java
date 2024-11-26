package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MissingVepMetaExceptionTest {

    @Test
    void getMessage() {
        assertEquals(
                "Metadata json is missing required VEP annotation.",
                new MissingVepMetaException().getMessage());
    }
}