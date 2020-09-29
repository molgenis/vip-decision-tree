package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

@ExtendWith(MockitoExtension.class)
class VepInfoMetadataMapperTest {

  private VepInfoMetadataMapper vepInfoMetadataMapper;
  @Mock
  VCFInfoHeaderLine headerLine;
  private Field vepField;
  private VepInfoSelector selector;

  @BeforeEach
  void setUp() {
    selector = new VepInfoSelector();
    vepInfoMetadataMapper = new VepInfoMetadataMapper(selector);

    vepField = Field.builder()
        .id("CSQ")
        .fieldType(FieldType.INFO)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(VARIABLE).build())
        .separator('|')
        .build();
  }

  @Test
  void canMap() {
    when(headerLine.getDescription()).thenReturn(
        "Consequence annotations from Ensembl VEP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|DISTANCE|STRAND|FLAGS|PICK|SYMBOL_SOURCE|HGNC_ID|GENE_PHENO|gnomAD_AF|gnomAD_AFR_AF|gnomAD_AMR_AF|gnomAD_ASJ_AF|gnomAD_EAS_AF|gnomAD_FIN_AF|gnomAD_NFE_AF|gnomAD_OTH_AF|gnomAD_SAS_AF|CLIN_SIG|SOMATIC|PHENO|PUBMED|CHECK_REF");
    assertTrue(vepInfoMetadataMapper.canMap(headerLine));
  }

  @Test
  void cantMapDesc() {
    when(headerLine.getDescription()).thenReturn(
        "Other annotations not from Ensembl VEP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|DISTANCE|STRAND|FLAGS|PICK|SYMBOL_SOURCE|HGNC_ID|GENE_PHENO|gnomAD_AF|gnomAD_AFR_AF|gnomAD_AMR_AF|gnomAD_ASJ_AF|gnomAD_EAS_AF|gnomAD_FIN_AF|gnomAD_NFE_AF|gnomAD_OTH_AF|gnomAD_SAS_AF|CLIN_SIG|SOMATIC|PHENO|PUBMED|CHECK_REF");
    assertFalse(vepInfoMetadataMapper.canMap(headerLine));
  }

  @Test
  void map() {
    when(headerLine.getID()).thenReturn("CSQ");
    when(headerLine.getDescription()).thenReturn(
        "Consequence annotations from Ensembl VEP. Format: Allele|cDNA_position|FLAGS|PICK|gnomAD_AF|PUBMED");

    NestedInfoHeaderLine actual = vepInfoMetadataMapper
        .map(headerLine);

    Map<String, NestedField> expectedMap = new HashMap<>();
    expectedMap.put("Allele", getFixedStringField("Allele", 0));
    expectedMap.put("cDNA_position", getFixedIntegerField("cDNA_position", 1));
    expectedMap.put("FLAGS", getVariableFlagField("FLAGS", 2));
    expectedMap.put("PICK", getFixedFlagField("PICK", 3));
    expectedMap.put("gnomAD_AF", getFixedFloatField("gnomAD_AF", 4));
    expectedMap.put("PUBMED", getVariableIntegerField("PUBMED", 5));
    assertEquals(NestedInfoHeaderLine.builder().nestedFields(expectedMap).build(), actual);
  }


  private NestedField getFixedFlagField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_NESTED)
        .nestedInfoSelector(selector)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.FLAG).build();
  }

  private NestedField getVariableFlagField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_NESTED).nestedInfoSelector(selector)
        .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
        .valueType(ValueType.FLAG)
        .separator('&').build();
  }

  private NestedField getVariableIntegerField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_NESTED).nestedInfoSelector(selector)
        .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
        .valueType(ValueType.INTEGER)
        .separator('&').build();
  }

  private NestedField getFixedIntegerField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_NESTED).nestedInfoSelector(selector)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.INTEGER).build();
  }

  private NestedField getFixedFloatField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_NESTED).nestedInfoSelector(selector)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.FLOAT).build();
  }

  private NestedField getFixedStringField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_NESTED).nestedInfoSelector(selector)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.STRING).build();
  }
}