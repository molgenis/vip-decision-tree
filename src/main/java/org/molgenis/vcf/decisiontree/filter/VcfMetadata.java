package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.COMMON;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.FORMAT;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO_NESTED;

import htsjdk.variant.vcf.VCFCompoundHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import java.util.Arrays;
import java.util.List;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.ValueCountBuilder;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.info.NestedInfoHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VcfNestedMetadata;

/**
 * {@link VCFHeader} wrapper that works with nested metadata (e.g. CSQ INFO fields).
 */
public class VcfMetadata {

  private static final String FIELD_TOKEN_SEPARATOR = "/";

  private final VCFHeader vcfHeader;
  private final VcfNestedMetadata nestedMetadata;
  private final boolean strict;

  public VcfMetadata(VCFHeader vcfHeader, VcfNestedMetadata nestedMetadata, boolean strict) {
    this.vcfHeader = requireNonNull(vcfHeader);
    this.nestedMetadata = requireNonNull(nestedMetadata);
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
      case INFO:
      case FORMAT:
        field = toCompoundField(fieldTokens, fieldType);
        break;
      case INFO_NESTED:
        field = toNestedField(fieldTokens, fieldType);
        break;
      default:
        throw new UnexpectedEnumException(fieldType);
    }

    return field;
  }

  private Field toNestedField(List<String> fieldTokens, FieldType fieldType) {
    if (fieldTokens.size() != 3) {
      throw new InvalidNumberOfTokensException(fieldTokens, fieldType, 2);
    }
    String field = fieldTokens.get(1);
    String nestedField = fieldTokens.get(2);

    NestedInfoHeaderLine nestedFieldMetadata = nestedMetadata.getNestedInfoHeaderLine(field);
    if (nestedFieldMetadata == null) {
      if (strict) {
        throw new UnknownFieldException(field, INFO);
      } else {
        return new MissingField(field);
      }
    }
    if (nestedFieldMetadata.hasField(nestedField)) {
      return nestedFieldMetadata.getField(nestedField);
    } else {
      if (strict) {
        throw new UnknownFieldException(nestedField, INFO_NESTED);
      } else {
        return new MissingField(nestedField);
      }
    }
  }

  private static FieldType toFieldType(List<String> fields) {
    String rootField = fields.get(0);

    FieldType fieldType;
    switch (rootField) {
      case "#CHROM":
      case "POS":
      case "ID":
      case "REF":
      case "ALT":
      case "QUAL":
      case "FILTER":
        fieldType = COMMON;
        break;
      case "INFO":
        fieldType = fields.size() > 2 ? INFO_NESTED : INFO;
        break;
      case "FORMAT":
        fieldType = FORMAT;
        break;
      default:
        throw new UnsupportedFieldException(rootField);
    }
    return fieldType;
  }

  private FieldImpl toCommonField(List<String> fieldTokens) {
    if (fieldTokens.size() > 1) {
      throw new InvalidNumberOfTokensException(fieldTokens, COMMON, 1);
    }

    ValueType valueType;
    ValueCount valueCount;
    String field = fieldTokens.get(0);
    switch (field) {
      case "#CHROM":
      case "REF":
        valueType = ValueType.STRING;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
        break;
      case "POS":
        valueType = ValueType.INTEGER;
        valueCount = ValueCount.builder().type(Type.FIXED).count(1).build();
        break;
      case "ID":
      case "FILTER":
      case "ALT":
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
        if (vcfCompoundHeaderLine == null) {
          throw new UnknownFieldException(field, fieldType);
        }
        break;
      case INFO:
        vcfCompoundHeaderLine = vcfHeader.getInfoHeaderLine(field);
        if (vcfCompoundHeaderLine == null && strict) {
          throw new UnknownFieldException(field, INFO_NESTED);
        }
        break;
      default:
        throw new UnexpectedEnumException(fieldType);
    }
    return vcfCompoundHeaderLine;
  }

  public VCFHeader unwrap() {
    return vcfHeader;
  }

  public VcfNestedMetadata getNestedMetadata() {
    return nestedMetadata;
  }
}
