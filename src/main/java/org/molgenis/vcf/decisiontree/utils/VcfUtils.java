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
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.decisiontree.filter.UnsupportedFieldException;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.utils.metadata.ValueType;

public class VcfUtils {

  public static final String FIELD_TOKEN_SEPARATOR = "/";

  private VcfUtils() {}

  public static @Nullable Integer getInfoAsInteger(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    Set<String> missingValues = getMissingValues(field);
    return getVcfValueAsInteger(value, missingValues);
  }

  public static List<Integer> getInfoAsIntegerList(VariantContext variantContext, Field field) {
    List<Integer> integerValues;

    Set<String> missingValues = getMissingValues(field);
    Object value = variantContext.getAttribute(field.getId());
    switch (value) {
      case null -> integerValues = List.of();
      case List<?> objectValues -> {
        int size = objectValues.size();
        if (size == 0) {
          integerValues = emptyList();
        } else if (size == 1) {
          integerValues =
              singletonList(getVcfValueAsInteger(objectValues.getFirst(), missingValues));
        } else {
          integerValues = new ArrayList<>(objectValues.size());
          for (Object objValue : objectValues) {
            integerValues.add(getVcfValueAsInteger(objValue, missingValues));
          }
        }
      }
      case String stringValue ->
          integerValues = singletonList(getInfoStringValueAsInteger(stringValue, missingValues));
      default -> throw new TypeConversionException(value, Integer.class);
    }

    return integerValues;
  }

  private static @Nullable Integer getVcfValueAsInteger(
      @Nullable Object objValue, Set<String> missingValues) {
    return switch (objValue) {
      case null -> null;
      case Integer integer -> integer;
      case String stringValue -> getInfoStringValueAsInteger(stringValue, missingValues);
      default -> throw new TypeConversionException(objValue, Integer.class);
    };
  }

  private static @Nullable Integer getInfoStringValueAsInteger(
      String infoStrValue, Set<String> missingValues) {
    Integer intValue;
    if (missingValues.contains(infoStrValue)) {
      intValue = null;
    } else {
      intValue = Integer.valueOf(infoStrValue);
    }
    return intValue;
  }

  public static @Nullable Double getInfoAsDouble(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    Set<String> missingValues = getMissingValues(field);
    return getVcfValueAsDouble(value, missingValues);
  }

  public static List<Double> getInfoAsDoubleList(VariantContext variantContext, Field field) {
    List<Double> doubleValues;
    Set<String> missingValues = getMissingValues(field);

    Object value = variantContext.getAttribute(field.getId());
    switch (value) {
      case null -> doubleValues = List.of();
      case List<?> objectValues -> {
        int size = objectValues.size();
        if (size == 0) {
          doubleValues = emptyList();
        } else if (size == 1) {
          doubleValues = singletonList(getVcfValueAsDouble(objectValues.getFirst(), missingValues));
        } else {
          doubleValues = new ArrayList<>(objectValues.size());
          for (Object objValue : objectValues) {
            doubleValues.add(getVcfValueAsDouble(objValue, missingValues));
          }
        }
      }
      case String string ->
          doubleValues = singletonList(getInfoStringValueAsDouble(string, missingValues));
      default -> throw new TypeConversionException(value, Double.class);
    }

    return doubleValues;
  }

  public static Set<String> getMissingValues(Field field) {
    return field.getFieldType() == FORMAT
        ? Set.of("", VCFConstants.MISSING_VALUE_v4)
        : Set.of(VCFConstants.MISSING_VALUE_v4);
  }

  private static @Nullable Double getVcfValueAsDouble(
      @Nullable Object objValue, Set<String> missingValues) {
    return switch (objValue) {
      case null -> null;
      case Double doubleVal -> doubleVal;
      case String string -> getInfoStringValueAsDouble(string, missingValues);
      default -> throw new TypeConversionException(objValue, Double.class);
    };
  }

  private static @Nullable Double getInfoStringValueAsDouble(
      String infoStrValue, Set<String> missingValues) {
    Double doubleValue;
    if (missingValues.contains(infoStrValue)) {
      doubleValue = null;
    } else {
      doubleValue = Double.valueOf(infoStrValue);
    }
    return doubleValue;
  }

  public static @Nullable String getInfoAsString(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    Set<String> missingValues = getMissingValues(field);
    return getVcfValueAsString(value, missingValues);
  }

  public static List<String> getInfoAsStringList(VariantContext variantContext, Field field) {
    List<String> strValues;
    String id = field.getId();
    Set<String> missingValues = getMissingValues(field);

    strValues = getInfoAsStringList(variantContext, id, missingValues);

    return strValues;
  }

  public static List<String> getInfoAsStringList(
      VariantContext variantContext, String id, Set<String> missingValues) {
    List<String> strValues;
    Object value = variantContext.getAttribute(id);
    switch (value) {
      case null -> strValues = List.of();
      case List<?> objectValues -> {
        int size = objectValues.size();
        if (size == 0) {
          strValues = emptyList();
        } else if (size == 1) {
          strValues = singletonList(getVcfValueAsString(objectValues.getFirst(), missingValues));
        } else {
          strValues = new ArrayList<>(objectValues.size());
          for (Object objValue : objectValues) {
            strValues.add(getVcfValueAsString(objValue, missingValues));
          }
        }
      }
      case String string ->
          strValues = singletonList(getInfoStringValueAsString(string, missingValues));
      default -> throw new TypeConversionException(value, String.class);
    }

    return strValues;
  }

  private static @Nullable String getVcfValueAsString(
      @Nullable Object objValue, Set<String> missingValues) {
    String strValue;
    if (objValue == null) {
      strValue = null;
    } else if (objValue instanceof String string) {
      strValue = getInfoStringValueAsString(string, missingValues);
    } else {
      throw new TypeConversionException(objValue, String.class);
    }
    return strValue;
  }

  private static @Nullable String getInfoStringValueAsString(
      String infoStrValue, Set<String> missingValues) {
    String stringValue;
    if (missingValues.contains(infoStrValue)) {
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

  public static @Nullable Object getTypedVcfValue(
      Field field, String stringValue, String separator) {
    Object value;
    if (separator == null) {
      value = getTypedVcfValue(field, stringValue);
    } else {
      List<String> values = Arrays.asList(stringValue.split(separator));
      value = values.stream().map(singleValue -> getTypedVcfValue(field, singleValue)).toList();
    }
    return value;
  }

  public static @Nullable Object getTypedVcfValue(Field field, String stringValue) {
    Object typedValue;
    ValueType valueType = field.getValueType();
    Set<String> missingValues = getMissingValues(field);
    typedValue =
        switch (valueType) {
          case INTEGER -> VcfUtils.getVcfValueAsInteger(stringValue, missingValues);
          case FLAG -> VcfUtils.getVcfValueAsBoolean(stringValue);
          case FLOAT -> VcfUtils.getVcfValueAsDouble(stringValue, missingValues);
          case CHARACTER, STRING, CATEGORICAL ->
              VcfUtils.getVcfValueAsString(stringValue, missingValues);
        };
    return typedValue;
  }

  public static Object getTypedVcfListValue(
      Field field, String stringValue, Set<String> missingValues) {
    String[] stringValues = stringValue.split(",", -1);
    List<Object> values = new ArrayList<>();
    for (String value : stringValues) {
      if (value == null || missingValues.contains(value)) {
        values.add(null);
      }
      values.add(getTypedVcfValue(field, value));
    }
    return values;
  }

  public static FieldType toFieldType(List<String> fields) {
    String rootField = fields.getFirst();

    return switch (rootField) {
      case "#CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER" -> COMMON;
      case "INFO" -> fields.size() > 2 ? INFO_VEP : INFO;
      case "FORMAT" -> fields.size() > 2 ? GENOTYPE : FORMAT;
      case "SAMPLE" -> SAMPLE;
      default -> throw new UnsupportedFieldException(rootField);
    };
  }
}
