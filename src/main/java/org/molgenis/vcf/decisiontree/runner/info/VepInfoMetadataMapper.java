package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Arrays.asList;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.NestedField.NestedFieldBuilder;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.springframework.stereotype.Component;

@Component
public class VepInfoMetadataMapper implements VepMetadataMapper {

  public static final String ALLELE_NUM = "ALLELE_NUM";

  private static final String INFO_DESCRIPTION_PREFIX =
      "Consequence annotations from Ensembl VEP. Format: ";

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    // match on the description since the INFO ID is configurable (default: CSQ)
    String description = vcfInfoHeaderLine.getDescription();
    return description.startsWith(INFO_DESCRIPTION_PREFIX);
  }

  @Override
  public VepHeaderLine map(VCFInfoHeaderLine vcfInfoHeaderLine) {
    Map<String, NestedField> nestedFields = new HashMap<>();
    int index = 0;
    FieldImpl vepField =
        FieldImpl.builder()
            .id(vcfInfoHeaderLine.getID())
            .fieldType(FieldType.INFO)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(VARIABLE).build())
            .separator('|')
            .build();
    for (String id : getNestedInfoIds(vcfInfoHeaderLine)) {
      nestedFields.put(id, mapNestedMetadataToField(id, index, vepField));
      index++;
    }
    return VepHeaderLine.builder().parentField(vepField)
        .nestedFields(nestedFields).build();
  }

  protected List<String> getNestedInfoIds(VCFInfoHeaderLine vcfInfoHeaderLine) {
    String description = vcfInfoHeaderLine.getDescription();
    String[] infoIds = description.substring(INFO_DESCRIPTION_PREFIX.length()).split("\\|", -1);
    return asList(infoIds);
  }

  protected NestedField mapNestedMetadataToField(
      String id, int index, FieldImpl vepField) {
    NestedFieldBuilder fieldBuilder =
        NestedField.nestedBuilder()
            .id(id)
            .index(index)
            .parent(vepField)
            .fieldType(FieldType.INFO_VEP);
    switch (id) {
      case "PICK", ALLELE_NUM:
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.INTEGER);
        break;
      case "Consequence", "Existing_variation", "CLIN_SIG", "FLAGS":
        fieldBuilder
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.STRING)
            .separator('&');
        break;
      case "PHENO", "PUBMED", "SOMATIC":
        fieldBuilder
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.INTEGER)
            .separator('&');
        break;
      case "STRAND", "HGNC_ID", "cDNA_position", "CDS_position", "Protein_position", "gnomAD_HN":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.INTEGER);
        break;
      case "gnomAD_AF", "gnomAD_AFR_AF", "gnomAD_AMR_AF", "gnomAD_ASJ_AF", "gnomAD_EAS_AF", "gnomAD_FIN_AF", "gnomAD_NFE_AF", "gnomAD_OTH_AF", "gnomAD_SAS_AF", "SpliceAI_pred_DS_AG", "SpliceAI_pred_DS_AL", "SpliceAI_pred_DS_DG", "SpliceAI_pred_DS_DL", "SIFT", "PolyPhen", "CAPICE_CL", "CAPICE_SC":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.FLOAT);
        break;
      default:
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.STRING);
    }
    return fieldBuilder.build();
  }
}
