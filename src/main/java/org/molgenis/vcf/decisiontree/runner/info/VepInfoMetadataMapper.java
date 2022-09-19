package org.molgenis.vcf.decisiontree.runner.info;

import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.molgenis.vcf.utils.metadata.FieldMetadataService;
import org.molgenis.vcf.utils.model.FieldMetadata;
import org.molgenis.vcf.utils.model.NumberType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class VepInfoMetadataMapper implements VepMetadataMapper {

  public static final String ALLELE_NUM = "ALLELE_NUM";
  private static final String INFO_DESCRIPTION_PREFIX =
      "Consequence annotations from Ensembl VEP. Format: ";

  private final FieldMetadataService vepMetadataService;

  public VepInfoMetadataMapper(
      @Qualifier("vepMetadataService") FieldMetadataService metadataService) {
    this.vepMetadataService = metadataService;
  }

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    // match on the description since the INFO ID is configurable (default: CSQ)
    String description = vcfInfoHeaderLine.getDescription();
    return description.startsWith(INFO_DESCRIPTION_PREFIX);
  }

  @Override
  public NestedHeaderLine map(VCFInfoHeaderLine vcfInfoHeaderLine) {
    Map<String, NestedField> nestedFields = new HashMap<>();

    FieldImpl vepField =
        FieldImpl.builder()
            .id(vcfInfoHeaderLine.getID())
            .fieldType(FieldType.INFO)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(VARIABLE).build())
            .separator('|')
            .build();

    FieldMetadata nestedMetadata = vepMetadataService.load(vcfInfoHeaderLine);
    for (Entry<String, org.molgenis.vcf.utils.model.NestedField> entry : nestedMetadata.getNestedFields()
        .entrySet()) {
      NestedField nestedField = mapNested(entry.getKey(), entry.getValue(), vepField);
      nestedFields.put(entry.getKey(), nestedField);
    }
    return NestedHeaderLine.builder().parentField(vepField).nestedFields(nestedFields).build();

  }

  private NestedField mapNested(String key, org.molgenis.vcf.utils.model.NestedField value,
      Field parent) {
    return NestedField.nestedBuilder().id(key).fieldType(FieldType.INFO_VEP)
        .valueType(mapValueType(value.getType()))
        .valueCount(
            mapValueCount(value.getNumberType(), value.getNumberCount(), value.isRequired()))
        .separator(value.getSeparator()).parent(parent).index(value.getIndex()).build();
  }

  private ValueType mapValueType(org.molgenis.vcf.utils.model.ValueType type) {
    return switch (type) {
      case INTEGER -> ValueType.INTEGER;
      case FLOAT -> ValueType.FLOAT;
      case FLAG -> ValueType.FLAG;
      case CHARACTER -> ValueType.CHARACTER;
      case STRING, CATEGORICAL -> ValueType.STRING;
      default -> throw new UnexpectedEnumException(type);
    };
  }

  private ValueCount mapValueCount(NumberType numberType, Integer numberCount, boolean required) {
    return ValueCount.builder().type(mapNumberType(numberType)).count(numberCount)
        .nullable(!required).build();
  }

  private Type mapNumberType(NumberType numberType) {
    return switch (numberType) {
      case NUMBER -> Type.FIXED;
      case PER_ALT -> Type.A;
      case PER_ALT_AND_REF -> Type.R;
      case PER_GENOTYPE -> Type.G;
      case OTHER -> VARIABLE;
      default -> throw new UnexpectedEnumException(numberType);
    };
  }
}
