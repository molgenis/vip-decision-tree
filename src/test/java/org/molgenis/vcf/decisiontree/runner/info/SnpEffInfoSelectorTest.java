package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.runner.info.SnpEffInfoSelector.ALLELE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

@ExtendWith(MockitoExtension.class)
class SnpEffInfoSelectorTest {

  @Mock
  NestedField alleleField;

  private SnpEffInfoSelector snpEffInfoSelector;

  @BeforeEach
  void setUp() {
    snpEffInfoSelector = new SnpEffInfoSelector();
  }

  @Test
  void select() {
    when(alleleField.getIndex()).thenReturn(0);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    Allele allele = Allele.builder().bases("A").index(1).build();
    snpEffInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder().nestedFields(nestedFields).build());
    assertEquals("A|X|Y", snpEffInfoSelector.select(List.of("A|X|Y"), allele));
  }

  @Test
  void selectNull() {
    when(alleleField.getIndex()).thenReturn(0);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    Allele allele = Allele.builder().bases("A").index(1).build();
    snpEffInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder().nestedFields(nestedFields).build());
    assertNull(snpEffInfoSelector.select(List.of("T|X|Y"), allele));
  }
}
