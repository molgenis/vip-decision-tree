package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class VariantTest {

  @Test
  void toDisplayString() {
    VcfMetadata vcfMetadata = mock(VcfMetadata.class);
    VcfRecord vcfRecord = mock(VcfRecord.class);
    int alleleIndex = 2;
    when(vcfRecord.getAltAllele(1)).thenReturn("ACTG");
    when(vcfRecord.toDisplayString()).thenReturn("<record_display_str>");
    Variant variant =
        Variant.builder()
            .vcfMetadata(vcfMetadata)
            .vcfRecord(vcfRecord)
            .alleleIndex(alleleIndex)
            .build();
    assertEquals("<record_display_str> -> ACTG", variant.toDisplayString());
  }
}
