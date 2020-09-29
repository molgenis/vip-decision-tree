package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.NestedField.NestedFieldBuilder;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.springframework.stereotype.Component;

@Component
public class VepInfoMetadataMapper implements NestedMetadataMapper {

  private static final String INFO_DESCRIPTION_PREFIX =
      "Consequence annotations from Ensembl VEP. Format: ";

  private final VepInfoSelector selector;

  public VepInfoMetadataMapper(VepInfoSelector selector) {
    this.selector = requireNonNull(selector);
  }

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    // match on the description since the INFO ID is configurable (default: CSQ)
    String description = vcfInfoHeaderLine.getDescription();
    return description.startsWith(INFO_DESCRIPTION_PREFIX);
  }

  @Override
  public Map<String, NestedField> map(VCFInfoHeaderLine vcfInfoHeaderLine) {
    Map<String, NestedField> nestedFields = new HashMap<>();
    int index = 0;
    for (String id : getNestedInfoIds(vcfInfoHeaderLine)) {
      Field vepField = Field.builder()
          .id(vcfInfoHeaderLine.getID())
          .fieldType(FieldType.INFO)
          .valueType(ValueType.STRING)
          .valueCount(ValueCount.builder().type(VARIABLE).build())
          .separator('|')
          .build();
      nestedFields.put(id, mapNestedMetadataToField(id, index, vepField));
      index++;
    }
    return nestedFields;
  }

  protected List<String> getNestedInfoIds(VCFInfoHeaderLine vcfInfoHeaderLine) {
    String description = vcfInfoHeaderLine.getDescription();
    String[] infoIds = description.substring(INFO_DESCRIPTION_PREFIX.length()).split("\\|", -1);
    return asList(infoIds);
  }

  protected NestedField mapNestedMetadataToField(String id, int index, Field vepField) {
    NestedFieldBuilder fieldBuilder = NestedField.nestedBuilder().id(id).index(index)
        .parent(vepField).fieldType(FieldType.INFO_NESTED).nestedInfoSelector(selector);
    switch (id) {
      case "PICK":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.FLAG);
        break;
      case "Consequence":
      case "Existing_variation":
      case "CLIN_SIG":
      case "FLAGS":
        fieldBuilder
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.FLAG)
            .separator('&');
        break;
      case "PHENO":
      case "PUBMED":
      case "SOMATIC":
        fieldBuilder
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.INTEGER)
            .separator('&');
        break;
      case "STRAND":
      case "HGNC_ID":
      case "cDNA_position":
      case "CDS_position":
      case "Protein_position":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.INTEGER);
        break;
      case "gnomAD_AF":
      case "gnomAD_AFR_AF":
      case "gnomAD_AMR_AF":
      case "gnomAD_ASJ_AF":
      case "gnomAD_EAS_AF":
      case "gnomAD_FIN_AF":
      case "gnomAD_NFE_AF":
      case "gnomAD_OTH_AF":
      case "gnomAD_SAS_AF":
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
