package org.molgenis.vcf.decisiontree.utils;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.COMMON;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.FORMAT;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.GENOTYPE;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO_VEP;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.SAMPLE;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.molgenis.vcf.decisiontree.filter.UnsupportedFieldException;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.molgenis.vcf.utils.metadata.ValueType;
import org.springframework.lang.Nullable;

public class VcfUtils {

  public static final String FIELD_TOKEN_SEPARATOR = "/";

  private VcfUtils() {
  }

  public static Integer getInfoAsInteger(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    String missingValue = getMissingValue(field);
    return getVcfValueAsInteger(value, missingValue);
  }

  public static List<Integer> getInfoAsIntegerList(VariantContext variantContext, Field field) {
    List<Integer> integerValues;

    String missingValue = getMissingValue(field);
    Object value = variantContext.getAttribute(field.getId());
    if (value == null) {
      integerValues = List.of();
    } else if (value instanceof List<?> objectValues) {
      int size = objectValues.size();
      if (size == 0) {
        integerValues = emptyList();
      } else if (size == 1) {
        integerValues = singletonList(getVcfValueAsInteger(objectValues.get(0), missingValue));
      } else {
        integerValues = new ArrayList<>(objectValues.size());
        for (Object objValue : objectValues) {
          integerValues.add(getVcfValueAsInteger(objValue, missingValue));
        }
      }
    } else if (value instanceof String stringValue) {
      integerValues = singletonList(getInfoStringValueAsInteger(stringValue, missingValue));
    } else {
      throw new TypeConversionException(value, Integer.class);
    }

    return integerValues;
  }

  private static @Nullable
  Integer getVcfValueAsInteger(@Nullable Object objValue, String missingValue) {
    Integer intValue;
    if (objValue == null) {
      intValue = null;
    } else if (objValue instanceof Integer integer) {
      intValue = integer;
    } else if (objValue instanceof String stringValue) {
      intValue = getInfoStringValueAsInteger(stringValue, missingValue);
    } else {
      throw new TypeConversionException(objValue, Integer.class);
    }
    return intValue;
  }

  private static @Nullable
  Integer getInfoStringValueAsInteger(String infoStrValue, String missingValue) {
    Integer intValue;
    if (infoStrValue.equals(missingValue)) {
      intValue = null;
    } else {
      intValue = Integer.valueOf(infoStrValue);
    }
    return intValue;
  }

  public static Double getInfoAsDouble(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    String missingValue = getMissingValue(field);
    return getVcfValueAsDouble(value, missingValue);
  }

  public static List<Double> getInfoAsDoubleList(VariantContext variantContext, Field field) {
    List<Double> doubleValues;
    String missingValue = getMissingValue(field);

    Object value = variantContext.getAttribute(field.getId());
    if (value == null) {
      doubleValues = List.of();
    } else if (value instanceof List<?> objectValues) {
      int size = objectValues.size();
      if (size == 0) {
        doubleValues = emptyList();
      } else if (size == 1) {
        doubleValues = singletonList(getVcfValueAsDouble(objectValues.get(0), missingValue));
      } else {
        doubleValues = new ArrayList<>(objectValues.size());
        for (Object objValue : objectValues) {
          doubleValues.add(getVcfValueAsDouble(objValue, missingValue));
        }
      }
    } else if (value instanceof String string) {
      doubleValues = singletonList(getInfoStringValueAsDouble(string, missingValue));
    } else {
      throw new TypeConversionException(value, Double.class);
    }

    return doubleValues;
  }

  private static String getMissingValue(Field field) {
    return field.getFieldType() == FORMAT ? "" : VCFConstants.MISSING_VALUE_v4;
  }

  private static @Nullable
  Double getVcfValueAsDouble(@Nullable Object objValue, String missingValue) {
    Double doubleValue;
    if (objValue == null) {
      doubleValue = null;
    } else if (objValue instanceof Double doubleVal) {
      doubleValue = doubleVal;
    } else if (objValue instanceof String string) {
      doubleValue = getInfoStringValueAsDouble(string, missingValue);
    } else {
      throw new TypeConversionException(objValue, Double.class);
    }
    return doubleValue;
  }

  private static @Nullable
  Double getInfoStringValueAsDouble(String infoStrValue, String missingValue) {
    Double doubleValue;
    if (infoStrValue.equals(missingValue)) {
      doubleValue = null;
    } else {
      doubleValue = Double.valueOf(infoStrValue);
    }
    return doubleValue;
  }

  public static String getInfoAsString(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    String missingValue = getMissingValue(field);
    return getVcfValueAsString(value, missingValue);
  }

  public static List<String> getInfoAsStringList(VariantContext variantContext, Field field) {
    List<String> strValues;
    String id = field.getId();
    String missingValue = getMissingValue(field);

    strValues = getInfoAsStringList(variantContext, id, missingValue);

    return strValues;
  }

  public static List<String> getInfoAsStringList(VariantContext variantContext, String id, String missingValue) {
    List<String> strValues;
    Object value = variantContext.getAttribute(id);
    if (value == null) {
      strValues = List.of();
    } else if (value instanceof List<?> objectValues) {
      int size = objectValues.size();
      if (size == 0) {
        strValues = emptyList();
      } else if (size == 1) {
        strValues = singletonList(getVcfValueAsString(objectValues.get(0), missingValue));
      } else {
        strValues = new ArrayList<>(objectValues.size());
        for (Object objValue : objectValues) {
          strValues.add(getVcfValueAsString(objValue, missingValue));
        }
      }
    } else if (value instanceof String string) {
      strValues = singletonList(getInfoStringValueAsString(string, missingValue));
    } else {
      throw new TypeConversionException(value, String.class);
    }

    return strValues;
  }

  private static @Nullable
  String getVcfValueAsString(@Nullable Object objValue, String missingValue) {
    String strValue;
    if (objValue == null) {
      strValue = null;
    } else if (objValue instanceof String string) {
      strValue = getInfoStringValueAsString(string, missingValue);
    } else {
      throw new TypeConversionException(objValue, String.class);
    }
    return strValue;
  }

  private static @Nullable
  String getInfoStringValueAsString(String infoStrValue, String missingValue) {
    String stringValue;
    if (infoStrValue.equals(missingValue)) {
      stringValue = null;
    } else {
      stringValue = infoStrValue;
    }
    return stringValue;
  }

  public static boolean getInfoAsBoolean(VariantContext variantContext, Field field) {
    Object objValue = variantContext.getAttribute(field.getId());
    return getVcfValueAsBoolean(objValue);
  }

  private static boolean getVcfValueAsBoolean(Object objValue) {
    boolean bool;

    if (objValue == null) {
      bool = false;
    } else if (objValue instanceof Boolean boolVal) {
      bool = boolVal;
    } else {
      throw new TypeConversionException(objValue, Boolean.class);
    }

    return bool;
  }

  public static Object getTypedVcfValue(Field field, String stringValue, String separator) {
    Object value;
    if (separator == null) {
      value = getTypedVcfValue(field, stringValue);
    } else {
      List<String> values = Arrays.asList(stringValue.split(separator));
      value = values.stream().map(singleValue -> getTypedVcfValue(field, singleValue)).toList();
    }
    return value;
  }

  public static Object getTypedVcfValue(Field field, String stringValue) {
    Object typedValue;
    ValueType valueType = field.getValueType();
    String missingValue = getMissingValue(field);
    switch (valueType) {
      case INTEGER:
        typedValue = VcfUtils.getVcfValueAsInteger(stringValue, missingValue);
        break;
      case FLAG:
        typedValue = VcfUtils.getVcfValueAsBoolean(stringValue);
        break;
      case FLOAT:
        typedValue = VcfUtils.getVcfValueAsDouble(stringValue, missingValue);
        break;
      case CHARACTER, STRING, CATEGORICAL:
        typedValue = VcfUtils.getVcfValueAsString(stringValue, missingValue);
        break;
      default:
        throw new UnexpectedEnumException(valueType);
    }
    return typedValue;
  }


  public static Object getTypedVcfListValue(Field field, String stringValue) {
    String[] stringValues = stringValue.split(",");
    List<Object> values = new ArrayList<>();
    for (String value : stringValues) {
      values.add(getTypedVcfValue(field, value));
    }
    return values;
  }

  public static FieldType toFieldType(List<String> fields) {
    String rootField = fields.get(0);

    FieldType fieldType;
    switch (rootField) {
      case "#CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER":
        fieldType = COMMON;
        break;
      case "INFO":
        fieldType = fields.size() > 2 ? INFO_VEP : INFO;
        break;
      case "FORMAT":
        fieldType = fields.size() > 2 ? GENOTYPE : FORMAT;
        break;
      case "SAMPLE":
        fieldType = SAMPLE;
        break;
      default:
        throw new UnsupportedFieldException(rootField);
    }
    return fieldType;
  }
}
