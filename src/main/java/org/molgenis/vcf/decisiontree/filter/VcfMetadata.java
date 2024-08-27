package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.*;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.FIELD_TOKEN_SEPARATOR;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.toFieldType;

import htsjdk.variant.vcf.VCFCompoundHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.ValueCountBuilder;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.molgenis.vcf.utils.metadata.MetadataService;
import org.molgenis.vcf.utils.model.FieldMetadata;
import org.molgenis.vcf.utils.model.NumberType;

/**
 * {@link VCFHeader} wrapper that works with nested metadata (e.g. CSQ INFO fields).
 */
public class VcfMetadata {

  private final VCFHeader vcfHeader;
  private final boolean strict;
  private final NestedHeaderLine nestedVepHeaderLine;
  private final NestedHeaderLine nestedGenotypeHeaderLine;

  private final MetadataService metadataService;

  public VcfMetadata(VCFHeader vcfHeader, NestedHeaderLine nestedVepHeaderLine,
                     NestedHeaderLine nestedGenotypeHeaderLine, MetadataService metadataService, boolean strict) {
    this.vcfHeader = requireNonNull(vcfHeader);
    this.nestedVepHeaderLine = requireNonNull(nestedVepHeaderLine);
    this.nestedGenotypeHeaderLine = requireNonNull(nestedGenotypeHeaderLine);
    this.metadataService = requireNonNull(metadataService);
    this.strict = strict;
  }

  public Field getField(String fieldId) {
    List<String> fieldTokens = Arrays.asList(fieldId.split(FIELD_TOKEN_SEPARATOR));

    Field field;
    FieldType fieldType = toFieldType(fieldTokens);
    field = switch (fieldType) {
      case COMMON -> toCommonField(fieldTokens);
      case INFO, FORMAT -> toCompoundField(fieldTokens, fieldType);
      case SAMPLE -> toSampleField(fieldTokens);
      case INFO_VEP -> toNestedField(fieldTokens, fieldType, nestedVepHeaderLine);
      case GENOTYPE -> toNestedField(fieldTokens, fieldType, nestedGenotypeHeaderLine);
      //noinspection UnnecessaryDefault
      default -> throw new UnexpectedEnumException(fieldType);
    };

    return field;
  }

  private static Field toSampleField(List<String> fieldTokens) {
    if (fieldTokens.size() != 2) {
      throw new InvalidNumberOfTokensException(fieldTokens, SAMPLE, 2);
    }

    ValueType valueType;
    ValueCount valueCount;
    String field = fieldTokens.get(1);
    switch (field.toUpperCase()) {
      case "PROBAND" -> {
        valueType = ValueType.FLAG;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
      }
      case "ID", "AFFECTED_STATUS", "SEX", "FATHER_ID", "MOTHER_ID", "FAMILY_ID" -> {
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
      }
      case "PHENOTYPES" -> {
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.VARIABLE).nullable(true).build();
      }
      default -> throw new UnsupportedFieldException(field);
    }
    return FieldImpl.builder()
        .id(field)
        .fieldType(SAMPLE)
        .valueType(valueType)
        .valueCount(valueCount)
        .build();
  }

  private Field toNestedField(List<String> fieldTokens, FieldType fieldType,
      NestedHeaderLine nestedHeaderLine) {
    if (fieldTokens.size() != 3) {
      throw new InvalidNumberOfTokensException(fieldTokens, fieldType, 2);
    }

    String field = fieldTokens.get(1);
    String nestedFieldId = fieldTokens.get(2);

    if (!field.equals(nestedHeaderLine.getParentField().getId())) {
      if (strict) {
        throw new UnsupportedNestedFieldException(field);
      } else {
        return new MissingField(field);
      }
    }

    Field nestedField = nestedHeaderLine.getField(nestedFieldId);
    if (nestedField instanceof MissingField && strict) {
      throw new UnknownFieldException(nestedFieldId, INFO_VEP);
    }
    return nestedField;
  }

  private FieldImpl toCommonField(List<String> fieldTokens) {
    if (fieldTokens.size() > 1) {
      throw new InvalidNumberOfTokensException(fieldTokens, COMMON, 1);
    }

    ValueType valueType;
    ValueCount valueCount;
    String field = fieldTokens.get(0);
    switch (field) {
      case "#CHROM", "REF" -> {
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
      }
      case "POS" -> {
        valueType = ValueType.INTEGER;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
      }
      case "ID", "FILTER", "ALT" -> {
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.VARIABLE).nullable(true).build();
      }
      case "QUAL" -> {
        valueType = ValueType.FLOAT;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).nullable(true).build();
      }
      default -> throw new UnsupportedFieldException(field);
    }
    return FieldImpl.builder()
        .id(field)
        .fieldType(COMMON)
        .valueType(valueType)
        .valueCount(valueCount)
        .build();
  }

  private Field toCompoundField(List<String> fieldTokens, FieldType fieldType) {
    //HERE?
    if (fieldTokens.size() != 2) {
      throw new InvalidNumberOfTokensException(fieldTokens, fieldType, 2);
    }
    String field = fieldTokens.get(1);
    if(fieldType == INFO){
      if(metadataService.getFieldMetadatas() != null && metadataService.getFieldMetadatas().getInfo().containsKey(field)){
        FieldMetadata fieldMetadata = metadataService.getFieldMetadatas().getInfo().get(field);
        return FieldImpl.builder().id(field).fieldType(INFO)
                .valueType(mapValueType(fieldMetadata.getField().getType()))
                .valueCount(
                        mapValueCount(fieldMetadata.getField().getNumberType(), fieldMetadata.getField().getNumberCount(), fieldMetadata.getField().isRequired()))
                .separator(fieldMetadata.getField().getSeparator()).build();
      }
    }
    if(fieldType == FORMAT) {
      if (metadataService.getFieldMetadatas() != null && metadataService.getFieldMetadatas().getFormat().containsKey(field)) {
        org.molgenis.vcf.utils.model.Field formatFieldMetadata = metadataService.getFieldMetadatas().getFormat().get(field);
        return FieldImpl.builder().id(field).fieldType(FORMAT)
                .valueType(mapValueType(formatFieldMetadata.getType()))
                .valueCount(
                        mapValueCount(formatFieldMetadata.getNumberType(), formatFieldMetadata.getNumberCount(), formatFieldMetadata.isRequired()))
                .separator(formatFieldMetadata.getSeparator()).build();
      }
    }
    VCFCompoundHeaderLine vcfCompoundHeaderLine = getVcfCompoundHeaderLine(fieldType, field);

    if (vcfCompoundHeaderLine == null) {
      return new MissingField(field);
    }

    ValueType valueType;
    VCFHeaderLineType lineType = vcfCompoundHeaderLine.getType();
    valueType = switch (lineType) {
      case Integer -> ValueType.INTEGER;
      case Float -> ValueType.FLOAT;
      case String -> ValueType.STRING;
      case Character -> ValueType.CHARACTER;
      case Flag -> ValueType.FLAG;
      //noinspection UnnecessaryDefault
      default -> throw new UnexpectedEnumException(lineType);
    };

    ValueCountBuilder builder = ValueCount.builder();
    VCFHeaderLineCount countType = vcfCompoundHeaderLine.getCountType();
    switch (countType) {
      case INTEGER -> {
        int count = vcfCompoundHeaderLine.getCount();
        builder.type(Type.FIXED).count(count).nullable(valueType != ValueType.FLAG);
      }
      case A -> builder.type(Type.A).nullable(true);
      case R -> builder.type(Type.R).nullable(true);
      case G -> builder.type(Type.G).nullable(true);
      case UNBOUNDED -> builder.type(Type.VARIABLE).nullable(true);
      default -> throw new UnexpectedEnumException(countType);
    }

    return FieldImpl.builder()
        .id(field)
        .fieldType(fieldType)
        .valueType(valueType)
        .valueCount(builder.build())
        .build();
}

  private ValueType mapValueType(org.molgenis.vcf.utils.model.ValueType type) {
    return switch (type) {
      case INTEGER -> ValueType.INTEGER;
      case FLOAT -> ValueType.FLOAT;
      case FLAG -> ValueType.FLAG;
      case CHARACTER -> ValueType.CHARACTER;
      case STRING, CATEGORICAL -> ValueType.STRING;
      case RANGE -> ValueType.RANGE;
      //noinspection UnnecessaryDefault
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
      //noinspection UnnecessaryDefault
      default -> throw new UnexpectedEnumException(numberType);
    };
  }
  private VCFCompoundHeaderLine getVcfCompoundHeaderLine(FieldType fieldType, String field) {
    VCFCompoundHeaderLine vcfCompoundHeaderLine;
    switch (fieldType) {
      case FORMAT -> {
        vcfCompoundHeaderLine = vcfHeader.getFormatHeaderLine(field);
        if (vcfCompoundHeaderLine == null && strict) {
          throw new UnknownFieldException(field, fieldType);
        }
      }
      case INFO -> {
        vcfCompoundHeaderLine = vcfHeader.getInfoHeaderLine(field);
        if (vcfCompoundHeaderLine == null && strict) {
          throw new UnknownFieldException(field, INFO_VEP);
        }
      }
      default -> throw new UnexpectedEnumException(fieldType);
    }
    return vcfCompoundHeaderLine;
  }

  public static boolean isSingleValueField(Field field) {
    return field.getValueCount().getType() == Type.FIXED && field.getValueCount().getCount() <= 1;
  }

  public Map<String, Integer> getSampleNameToOffset() {
    return vcfHeader.getSampleNameToOffset();
  }

  public VCFHeader unwrap() {
    return vcfHeader;
  }

  public NestedHeaderLine getVepHeaderLine() {
    return nestedVepHeaderLine;
  }
}
