package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VepMetadataParserImplTest {

  @Mock
  VepMetadataMapper mapper;
  @Mock
  VCFHeader vcfHeader;
  @Mock
  VCFInfoHeaderLine headerLine;

  private VepMetadataParserImpl nestedMetadataServiceImpl;

  @BeforeEach
  void setUp() {
    when(vcfHeader.getInfoHeaderLines()).thenReturn(Collections.singletonList(headerLine));

    nestedMetadataServiceImpl =
        new VepMetadataParserImpl(mapper);
  }

  @Test
  void mapMatchingHeader() {
    when(mapper.canMap(headerLine)).thenReturn(true);

    nestedMetadataServiceImpl.map(vcfHeader);

    verify(mapper).map(headerLine);
  }

  @Test
  void mapNoMatchingHeader() {
    when(mapper.canMap(headerLine)).thenReturn(false);

    assertThrows(MissingVepException.class, () -> {
      nestedMetadataServiceImpl.map(vcfHeader);
    });
  }
}