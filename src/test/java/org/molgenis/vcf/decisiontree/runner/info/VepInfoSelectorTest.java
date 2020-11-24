package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.ALLELE_NUM;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.PICK;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.PREFERRED;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
  @Mock
  NestedField preferredField;

  private VepInfoSelector vepInfoSelector;

  @BeforeEach
  void setUp() {
    vepInfoSelector = new VepInfoSelector();
  }

  @Test
  void selectSingle() {
    Allele allele = Allele.builder().bases("A").index(1).build();
    vepInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder().nestedFields(Map.of(ALLELE_NUM, alleleField)).build());
    assertEquals("1|A", vepInfoSelector.select(List.of("1|A"), allele));
  }

  @Test
  void selectNoAlleleMatch() {
    Allele allele = Allele.builder().bases("A").index(1).build();
    vepInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder().nestedFields(Map.of(ALLELE_NUM, alleleField)).build());
    assertNull(vepInfoSelector.select(List.of("2|A"), allele));
  }

  @Test
  void selectPickField() {
    when(pickField.getIndex()).thenReturn(1);
    Allele allele = Allele.builder().bases("A").index(1).build();
    vepInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder()
            .nestedFields(Map.of(ALLELE_NUM, alleleField, PICK, pickField))
            .build());
    assertEquals("1|1|C", vepInfoSelector.select(List.of("1|0|A", "2|1|B", "1|1|C"), allele));
  }

  private static Stream<Arguments> selectPreferredFieldProvider() {
    return Stream.of(
        Arguments.of(List.of("1|0|A", "2|1|B", "1|1|C"), "1|1|C"),
        Arguments.of(List.of("1|1|A", "2|1|B", "1|1|C"), "1|1|A"),
        Arguments.of(List.of("1|0|A", "2|1|B", "1|0|C"), "1|0|A")
    );
  }

  @ParameterizedTest
  @MethodSource("selectPreferredFieldProvider")
  void selectPreferredField(List<String> infoValues, String expectedInfoValue) {
    when(preferredField.getIndex()).thenReturn(1);
    Allele allele = Allele.builder().bases("A").index(1).build();
    vepInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder()
            .nestedFields(Map.of(ALLELE_NUM, alleleField, PREFERRED, preferredField))
            .build());
    assertEquals(expectedInfoValue, vepInfoSelector.select(infoValues, allele));
  }

  @Test
  void selectPickFieldAndPreferredField() {
    when(preferredField.getIndex()).thenReturn(2);
    Allele allele = Allele.builder().bases("A").index(1).build();
    vepInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder()
            .nestedFields(
                Map.of(ALLELE_NUM, alleleField, PICK, pickField, PREFERRED, preferredField))
            .build());
    assertEquals(
        "1|0|1|C", vepInfoSelector.select(List.of("1|1|0|A", "2|1|0|B", "1|0|1|C"), allele));
  }

  @Test
  void selectPickFieldAndPreferredFieldNoPreferred() {
    when(pickField.getIndex()).thenReturn(1);
    when(preferredField.getIndex()).thenReturn(2);
    Allele allele = Allele.builder().bases("A").index(1).build();
    vepInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder()
            .nestedFields(
                Map.of(ALLELE_NUM, alleleField, PICK, pickField, PREFERRED, preferredField))
            .build());
    assertEquals(
        "1|1|0|A", vepInfoSelector.select(List.of("1|1|0|A", "2|1|0|B", "1|0|0|C"), allele));
  }

  @Test
  void selectNoPickFieldAndNoPreferredField() {
    when(pickField.getIndex()).thenReturn(1);
    when(preferredField.getIndex()).thenReturn(2);
    Allele allele = Allele.builder().bases("A").index(1).build();
    vepInfoSelector.setNestedInfoHeaderLine(
        NestedInfoHeaderLine.builder()
            .nestedFields(
                Map.of(ALLELE_NUM, alleleField, PICK, pickField, PREFERRED, preferredField))
            .build());
    assertEquals(
        "1|0|0|B", vepInfoSelector.select(List.of("2|0|0|A", "1|0|0|B", "1|0|0|C"), allele));
  }

  @Test
  void setNestedInfoHeaderLineMissingAlleleNum() {
    NestedInfoHeaderLine nestedInfoHeaderLine =
        NestedInfoHeaderLine.builder().nestedFields(Map.of()).build();
    assertThrows(
        MissingRequiredNestedValueException.class,
        () -> vepInfoSelector.setNestedInfoHeaderLine(nestedInfoHeaderLine));
  }
}
