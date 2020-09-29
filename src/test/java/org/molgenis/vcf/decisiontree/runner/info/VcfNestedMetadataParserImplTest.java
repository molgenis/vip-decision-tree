package org.molgenis.vcf.decisiontree.runner.info;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VcfNestedMetadataParserImplTest {

  @Mock NestedMetadataMapper mapper;
  @Mock VCFHeader vcfHeader;
  @Mock VCFInfoHeaderLine headerLine;

  private VcfNestedMetadataParserImpl nestedMetadataServiceImpl;

  @BeforeEach
  void setUp() {
    when(vcfHeader.getInfoHeaderLines()).thenReturn(Collections.singletonList(headerLine));
    List<NestedMetadataMapper> mappers = new ArrayList<>();
    mappers.add(mapper);
    nestedMetadataServiceImpl =
        new VcfNestedMetadataParserImpl(mappers);
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

    nestedMetadataServiceImpl.map(vcfHeader);

    verify(mapper).canMap(headerLine);
    verifyNoMoreInteractions(mapper);
  }
}