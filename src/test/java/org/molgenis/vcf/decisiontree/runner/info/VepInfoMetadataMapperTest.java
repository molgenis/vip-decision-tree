package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
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
  private FieldImpl vepField;

  @BeforeEach
  void setUp() {

    vepInfoMetadataMapper = new VepInfoMetadataMapper();

    vepField = FieldImpl.builder()
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
    when(headerLine.getDescription())
        .thenReturn(
            "Consequence annotations from Ensembl VEP. Format: Allele|cDNA_position|FLAGS|PICK|gnomAD_AF|gnomAD_HN|PUBMED|CAPICE_CL|CAPICE_SC|clinVar|clinVar_CLNSIG|clinVar_CLNSIGINCL|clinVar_CLNREVSTAT");

    NestedHeaderLine actual = vepInfoMetadataMapper
        .map(headerLine);
    Field vepField = FieldImpl.builder().id("CSQ").fieldType(FieldType.INFO)
        .valueType(ValueType.STRING).valueCount(ValueCount.builder()
            .type(VARIABLE).build()).separator('|').build();

    Map<String, NestedField> expectedMap = new HashMap<>();
    expectedMap.put("Allele", getFixedStringField("Allele", 0));
    expectedMap.put("cDNA_position", getFixedIntegerField("cDNA_position", 1));
    expectedMap.put("FLAGS", getVariableIntegerField("FLAGS", 2));
    expectedMap.put("PICK", getFixedIntegerField("PICK", 3));
    expectedMap.put("gnomAD_AF", getFixedFloatField("gnomAD_AF", 4));
    expectedMap.put("gnomAD_HN", getFixedFloatField("gnomAD_HN", 5));
    expectedMap.put("PUBMED", getVariableIntegerField("PUBMED", 6));
    expectedMap.put("CAPICE_CL", getFixedStringField("CAPICE_CL", 7));
    expectedMap.put("CAPICE_SC", getVariableIntegerField("CAPICE_SC", 8));
    expectedMap.put("clinVar", getVariableIntegerField("clinVar", 9));
    expectedMap.put("clinVar_CLNSIG", getVariableIntegerField("clinVar_CLNSIG", 10));
    expectedMap.put("clinVar_CLNSIGINCL", getVariableIntegerField("clinVar_CLNSIGINCL", 11));
    expectedMap.put("clinVar_CLNREVSTAT", getVariableIntegerField("clinVar_CLNREVSTAT", 12));
    assertEquals(NestedHeaderLine.builder().nestedFields(expectedMap).parentField(vepField).build(),
        actual);
  }

  private NestedField getVariableIntegerField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
        .valueType(ValueType.INTEGER)
        .separator('&').build();
  }

  private NestedField getFixedIntegerField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.INTEGER).build();
  }

  private NestedField getFixedFloatField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.FLOAT).build();
  }

  private NestedField getFixedStringField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.STRING).build();
  }
}