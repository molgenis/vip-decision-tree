package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.ALLELE_NUM;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.PICK;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

@ExtendWith(MockitoExtension.class)
class VepInfoSelectorTest {

  @Mock
  NestedField alleleField;
  @Mock
  NestedField pickField;

  private VepInfoSelector vepInfoSelector;

  @BeforeEach
  void setUp() {
    vepInfoSelector = new VepInfoSelector();
  }

  @Test
  void isMatch() {
    when(pickField.getIndex()).thenReturn(1);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    nestedFields.put(PICK, pickField);
    Allele allele = Allele.builder().bases("A").index(1).build();
    assertTrue(
        vepInfoSelector.isMatch(
            "1|1|Y|Z", allele, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void noMatch() {
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    nestedFields.put(PICK, pickField);
    Allele allele = Allele.builder().bases("A").index(1).build();
    assertFalse(
        vepInfoSelector.isMatch(
            "2|1|Y|Z", allele, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void isMatchMulti() {
    when(pickField.getIndex()).thenReturn(1);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    nestedFields.put(PICK, pickField);
    Allele allele = Allele.builder().bases("A").index(1).build();
    assertTrue(
        vepInfoSelector.isMatch(
            "1|1|Y|Z", allele, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void isMatchNoPick() {
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    Allele allele = Allele.builder().bases("A").index(1).build();
    assertTrue(
        vepInfoSelector.isMatch(
            "1|Y|Z", allele, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }
}
