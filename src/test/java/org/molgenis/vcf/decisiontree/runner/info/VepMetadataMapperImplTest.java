package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.utils.metadata.ValueCount.Type.*;

import htsjdk.variant.vcf.VCFHeader;
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
import org.molgenis.vcf.utils.metadata.*;
import org.molgenis.vcf.utils.model.metadata.FieldMetadata;
import org.molgenis.vcf.utils.model.metadata.FieldMetadatas;
import org.molgenis.vcf.utils.model.metadata.NestedFieldMetadata;

@ExtendWith(MockitoExtension.class)
class VepMetadataMapperImplTest {

  private VepMetadataMapper vepMetadataMapper;

  @Mock
  FieldMetadataService fieldMetadataService;

  @Mock
  VCFInfoHeaderLine headerLine;



  @BeforeEach
  void setUp(){
    vepMetadataMapper = new VepMetadataMapperImpl(fieldMetadataService);
  }

  @Test
  void canMap() {
    when(headerLine.getDescription()).thenReturn(
        "Consequence annotations from Ensembl VEP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|DISTANCE|STRAND|FLAGS|PICK|SYMBOL_SOURCE|HGNC_ID|GENE_PHENO|gnomAD_AF|gnomAD_AFR_AF|gnomAD_AMR_AF|gnomAD_ASJ_AF|gnomAD_EAS_AF|gnomAD_FIN_AF|gnomAD_NFE_AF|gnomAD_OTH_AF|gnomAD_SAS_AF|CLIN_SIG|SOMATIC|PHENO|PUBMED|CHECK_REF");
    assertTrue(vepMetadataMapper.canMap(headerLine));
  }

  @Test
  void cantMapDesc() {
    when(headerLine.getDescription()).thenReturn(
        "Other annotations not from Ensembl VEP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|DISTANCE|STRAND|FLAGS|PICK|SYMBOL_SOURCE|HGNC_ID|GENE_PHENO|gnomAD_AF|gnomAD_AFR_AF|gnomAD_AMR_AF|gnomAD_ASJ_AF|gnomAD_EAS_AF|gnomAD_FIN_AF|gnomAD_NFE_AF|gnomAD_OTH_AF|gnomAD_SAS_AF|CLIN_SIG|SOMATIC|PHENO|PUBMED|CHECK_REF");
    assertFalse(vepMetadataMapper.canMap(headerLine));
  }

  @Test
  void map() {
    VCFHeader vcfHeader = mock(VCFHeader.class);
    VCFInfoHeaderLine csqInfoHeaderLine = mock(VCFInfoHeaderLine.class);
    when(csqInfoHeaderLine.getID()).thenReturn("CSQ");
    when(vcfHeader.getInfoHeaderLine("CSQ")).thenReturn(csqInfoHeaderLine);

    NestedFieldMetadata nestedStrandMeta = NestedFieldMetadata.builder().index(0)
            .label("STRAND").description("STRAND")
            .type(ValueType.INTEGER).numberType(FIXED).numberCount(1).build();
    NestedFieldMetadata nestedTestMeta = NestedFieldMetadata.builder().index(1)
            .label("TEST label").description("TEST desc").type(ValueType.INTEGER)
            .numberType(ValueCount.Type.R).build();
    FieldMetadata csqMeta = FieldMetadata.builder().label("CSQ").description("Consequence annotations from Ensembl VEP. Format: STRAND|TEST").numberType(ValueCount.Type.VARIABLE).type(ValueType.STRING).numberType(ValueCount.Type.VARIABLE).nestedFields(Map.of("STRAND", nestedStrandMeta, "TEST", nestedTestMeta)).build();
    FieldMetadatas fieldMetadatas = FieldMetadatas.builder().info(Map.of("CSQ", csqMeta)).format(Map.of()).build();
    when(fieldMetadataService.load(vcfHeader)).thenReturn(fieldMetadatas);

    NestedHeaderLine actual = vepMetadataMapper
            .map("CSQ", vcfHeader);

    Field vepField = FieldImpl.builder().id("CSQ").fieldType(FieldType.INFO)
        .valueType(ValueType.STRING).valueCount(ValueCount.builder()
            .type(VARIABLE).build()).separator('|').build();
    Map<String, NestedField> expectedMap = new HashMap<>();
    expectedMap.put("STRAND", NestedField.nestedBuilder().id("STRAND").index(0).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .valueType(ValueType.INTEGER).build());
    expectedMap.put("TEST", NestedField.nestedBuilder().id("TEST").index(1).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(ValueCount.Type.R).build())
        .valueType(ValueType.STRING).build());
    NestedHeaderLine expected = NestedHeaderLine.builder().nestedFields(expectedMap).parentField(vepField).build();
    assertEquals(expected, actual);
  }

}