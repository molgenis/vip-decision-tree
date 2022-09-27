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
import org.molgenis.vcf.utils.metadata.FieldMetadataService;
import org.molgenis.vcf.utils.model.FieldMetadata;
import org.molgenis.vcf.utils.model.NumberType;

@ExtendWith(MockitoExtension.class)
class VepInfoMetadataMapperTest {

  private VepInfoMetadataMapper vepInfoMetadataMapper;

  @Mock
  VCFInfoHeaderLine headerLine;
  @Mock
  FieldMetadataService metadataService;

  private FieldImpl vepField;

  @BeforeEach
  void setUp() {

    vepInfoMetadataMapper = new VepInfoMetadataMapper(metadataService);

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
    when(headerLine.getID()).thenReturn(
        "CSQ");
    org.molgenis.vcf.utils.model.Field vepUtilsField = org.molgenis.vcf.utils.model.Field.builder()
        .type(org.molgenis.vcf.utils.model.ValueType.STRING).numberType(NumberType.OTHER)
        .separator('|').required(true).label("CSQ").description("CSQ").build();
    HashMap<String, org.molgenis.vcf.utils.model.NestedField> vepMeta = new HashMap<>();
    vepMeta.put("Allele", org.molgenis.vcf.utils.model.NestedField.builder().index(4).numberCount(1)
        .numberType(NumberType.NUMBER)
        .type(org.molgenis.vcf.utils.model.ValueType.STRING).label("Allele").description("Allele")
        .build());
    vepMeta.put("field2", org.molgenis.vcf.utils.model.NestedField.builder().index(3).numberCount(1)
        .numberType(NumberType.PER_ALT)
        .type(org.molgenis.vcf.utils.model.ValueType.INTEGER).label("Field2").description("Field2")
        .build());
    vepMeta.put("field3", org.molgenis.vcf.utils.model.NestedField.builder().index(2).numberCount(1)
        .numberType(NumberType.PER_GENOTYPE)
        .type(org.molgenis.vcf.utils.model.ValueType.CHARACTER).label("Field3")
        .description("Field3")
        .build());
    vepMeta.put("field4", org.molgenis.vcf.utils.model.NestedField.builder().index(1).numberCount(1)
        .numberType(NumberType.PER_ALT_AND_REF)
        .type(org.molgenis.vcf.utils.model.ValueType.CATEGORICAL).label("Field4")
        .description("Field4")
        .build());
    when(metadataService.load(headerLine)).thenReturn(
        FieldMetadata.builder().nestedFields(vepMeta).build());

    NestedHeaderLine actual = vepInfoMetadataMapper
        .map(headerLine);

    Field vepField = FieldImpl.builder().id("CSQ").fieldType(FieldType.INFO)
        .valueType(ValueType.STRING).valueCount(ValueCount.builder()
            .type(VARIABLE).build()).separator('|').build();
    Map<String, NestedField> expectedMap = new HashMap<>();
    expectedMap.put("Allele", NestedField.nestedBuilder().id("Allele").index(4).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.STRING).build());
    expectedMap.put("field2", NestedField.nestedBuilder().id("field2").index(3).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(Type.R).build())
        .valueType(ValueType.INTEGER).build());
    expectedMap.put("field3", NestedField.nestedBuilder().id("field3").index(2).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(Type.G).build())
        .valueType(ValueType.CHARACTER).build());
    expectedMap.put("field4", NestedField.nestedBuilder().id("field4").index(1).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(Type.A).build())
        .valueType(ValueType.STRING).build());
    assertEquals(NestedHeaderLine.builder().nestedFields(expectedMap).parentField(vepField).build(),
        actual);
  }

}