package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.utils.metadata.ValueCount.Type.*;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.FileNotFoundException;
import java.nio.file.Path;
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
import org.molgenis.vcf.utils.metadata.FieldMetadataService;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;
import org.molgenis.vcf.utils.model.metadata.FieldMetadatas;
import org.springframework.util.ResourceUtils;

@ExtendWith(MockitoExtension.class)
class VepMetadataMapperImplTest {

  private VepMetadataMapper vepMetadataMapper;

  @Mock
  VCFInfoHeaderLine headerLine;

  @Mock
  FieldMetadataService fieldMetadataService;

  @BeforeEach
  void setUp() throws FileNotFoundException {
    String metadataFile = ResourceUtils.getFile("classpath:field_metadata.json").toString();

    vepMetadataMapper = new VepMetadataMapperImpl(Path.of(metadataFile));
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

    FieldMetadatas TODO = null;//FIXME
    when(fieldMetadataService.load(vcfHeader)).thenReturn(TODO);

    NestedHeaderLine actual = vepMetadataMapper
        .map("CSQ", vcfHeader);

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
        .valueCount(ValueCount.builder().type(ValueCount.Type.R).build())
        .valueType(ValueType.INTEGER).build());
    expectedMap.put("field3", NestedField.nestedBuilder().id("field3").index(2).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(ValueCount.Type.G).build())
        .valueType(ValueType.CHARACTER).build());
    expectedMap.put("field4", NestedField.nestedBuilder().id("field4").index(1).parent(vepField)
        .fieldType(FieldType.INFO_VEP)
        .valueCount(ValueCount.builder().type(ValueCount.Type.A).build())
        .valueType(ValueType.STRING).build());
    assertEquals(NestedHeaderLine.builder().nestedFields(expectedMap).parentField(vepField).build(),
        actual);
  }

}