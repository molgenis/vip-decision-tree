package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.COMMON;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO_VEP;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.SAMPLE;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.FIELD_TOKEN_SEPARATOR;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.toFieldType;

import htsjdk.variant.vcf.VCFCompoundHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.ValueCountBuilder;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;

/**
 * {@link VCFHeader} wrapper that works with nested metadata (e.g. CSQ INFO fields).
 */
public class VcfMetadata {

  private final VCFHeader vcfHeader;
  private final boolean strict;
  private final NestedHeaderLine nestedVepHeaderLine;
  private final NestedHeaderLine nestedGenotypeHeaderLine;

  public VcfMetadata(VCFHeader vcfHeader, NestedHeaderLine nestedVepHeaderLine,
      NestedHeaderLine nestedGenotypeHeaderLine, boolean strict) {
    this.vcfHeader = requireNonNull(vcfHeader);
    this.nestedVepHeaderLine = requireNonNull(nestedVepHeaderLine);
    this.nestedGenotypeHeaderLine = requireNonNull(nestedGenotypeHeaderLine);
    this.strict = requireNonNull(strict);
  }

  public Field getField(String fieldId) {
    List<String> fieldTokens = Arrays.asList(fieldId.split(FIELD_TOKEN_SEPARATOR));

    Field field;
    FieldType fieldType = toFieldType(fieldTokens);
    switch (fieldType) {
      case COMMON:
        field = toCommonField(fieldTokens);
        break;
      case INFO, FORMAT:
        field = toCompoundField(fieldTokens, fieldType);
        break;
      case SAMPLE:
        field = toSampleField(fieldTokens);
        break;
      case INFO_VEP:
        field = toNestedField(fieldTokens, fieldType, nestedVepHeaderLine);
        break;
      case GENOTYPE:
        field = toNestedField(fieldTokens, fieldType, nestedGenotypeHeaderLine);
        break;
      default:
        throw new UnexpectedEnumException(fieldType);
    }

    return field;
  }

  private Field toSampleField(List<String> fieldTokens) {
    if (fieldTokens.size() != 2) {
      throw new InvalidNumberOfTokensException(fieldTokens, SAMPLE, 2);
    }

    ValueType valueType;
    ValueCount valueCount;
    String field = fieldTokens.get(1);
    switch (field.toUpperCase()) {
      case "PROBAND":
        valueType = ValueType.FLAG;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
        break;
      case "AFFECTED_STATUS", "SEX", "FATHER", "MOTHER", "FAMILY":
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
        break;
      case "PHENOTYPES":
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.VARIABLE).nullable(true).build();
        break;
      default:
        throw new UnsupportedFieldException(field);
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
      case "#CHROM", "REF":
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
        break;
      case "POS":
        valueType = ValueType.INTEGER;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
        break;
      case "ID", "FILTER", "ALT":
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.VARIABLE).nullable(true).build();
        break;
      case "QUAL":
        valueType = ValueType.FLOAT;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).nullable(true).build();
        break;
      default:
        throw new UnsupportedFieldException(field);
    }
    return FieldImpl.builder()
        .id(field)
        .fieldType(COMMON)
        .valueType(valueType)
        .valueCount(valueCount)
        .build();
  }

  private Field toCompoundField(List<String> fieldTokens, FieldType fieldType) {
    if (fieldTokens.size() != 2) {
      throw new InvalidNumberOfTokensException(fieldTokens, fieldType, 2);
    }
    String field = fieldTokens.get(1);
    VCFCompoundHeaderLine vcfCompoundHeaderLine = getVcfCompoundHeaderLine(fieldType, field);

    if (vcfCompoundHeaderLine == null) {
      return new MissingField(field);
    }

    ValueType valueType;
    VCFHeaderLineType lineType = vcfCompoundHeaderLine.getType();
    switch (lineType) {
      case Integer:
        valueType = ValueType.INTEGER;
        break;
      case Float:
        valueType = ValueType.FLOAT;
        break;
      case String:
        valueType = ValueType.STRING;
        break;
      case Character:
        valueType = ValueType.CHARACTER;
        break;
      case Flag:
        valueType = ValueType.FLAG;
        break;
      default:
        throw new UnexpectedEnumException(lineType);
    }

    ValueCountBuilder builder = ValueCount.builder();
    VCFHeaderLineCount countType = vcfCompoundHeaderLine.getCountType();
    switch (countType) {
      case INTEGER:
        int count = vcfCompoundHeaderLine.getCount();
        builder.type(Type.FIXED).count(count).nullable(valueType != ValueType.FLAG);
        break;
      case A:
        builder.type(Type.A).nullable(true);
        break;
      case R:
        builder.type(Type.R).nullable(true);
        break;
      case G:
        builder.type(Type.G).nullable(true);
        break;
      case UNBOUNDED:
        builder.type(Type.VARIABLE).nullable(true);
        break;
      default:
        throw new UnexpectedEnumException(countType);
    }

    return FieldImpl.builder()
        .id(field)
        .fieldType(fieldType)
        .valueType(valueType)
        .valueCount(builder.build())
        .build();
  }

  private VCFCompoundHeaderLine getVcfCompoundHeaderLine(FieldType fieldType, String field) {
    VCFCompoundHeaderLine vcfCompoundHeaderLine;
    switch (fieldType) {
      case FORMAT:
        vcfCompoundHeaderLine = vcfHeader.getFormatHeaderLine(field);
        if (vcfCompoundHeaderLine == null && strict) {
          throw new UnknownFieldException(field, fieldType);
        }
        break;
      case INFO:
        vcfCompoundHeaderLine = vcfHeader.getInfoHeaderLine(field);
        if (vcfCompoundHeaderLine == null && strict) {
          throw new UnknownFieldException(field, INFO_VEP);
        }
        break;
      default:
        throw new UnexpectedEnumException(fieldType);
    }
    return vcfCompoundHeaderLine;
  }

  public static boolean isSingleValueField(Field field) {
    return field.getValueCount().getType() == Type.FIXED && field.getValueCount().getCount() == 1;
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
