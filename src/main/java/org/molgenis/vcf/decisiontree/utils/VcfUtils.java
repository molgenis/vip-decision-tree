package org.molgenis.vcf.decisiontree.utils;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoMetadataMapper.ALLELE_NUM;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.VcfRecord;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.info.VepHeaderLine;
import org.springframework.lang.Nullable;

public class VcfUtils {

  private VcfUtils() {
  }

  public static Integer getInfoAsInteger(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    return getInfoValueAsInteger(value);
  }

  public static List<Integer> getInfoAsIntegerList(VariantContext variantContext, Field field) {
    List<Integer> integerValues;

    Object value = variantContext.getAttribute(field.getId());
    if (value == null) {
      integerValues = List.of();
    } else if (value instanceof List<?> objectValues) {
      int size = objectValues.size();
      if (size == 0) {
        integerValues = emptyList();
      } else if (size == 1) {
        integerValues = singletonList(getInfoValueAsInteger(objectValues.get(0)));
      } else {
        integerValues = new ArrayList<>(objectValues.size());
        for (Object objValue : objectValues) {
          integerValues.add(getInfoValueAsInteger(objValue));
        }
      }
    } else if (value instanceof String stringValue) {
      integerValues = singletonList(getInfoStringValueAsInteger(stringValue));
    } else {
      throw new TypeConversionException(value, Integer.class);
    }

    return integerValues;
  }

  private static @Nullable
  Integer getInfoValueAsInteger(@Nullable Object objValue) {
    Integer intValue;
    if (objValue == null) {
      intValue = null;
    } else if (objValue instanceof Integer integer) {
      intValue = integer;
    } else if (objValue instanceof String stringValue) {
      intValue = getInfoStringValueAsInteger(stringValue);
    } else {
      throw new TypeConversionException(objValue, Integer.class);
    }
    return intValue;
  }

  private static @Nullable
  Integer getInfoStringValueAsInteger(String infoStrValue) {
    Integer intValue;
    if (infoStrValue.equals(VCFConstants.MISSING_VALUE_v4)) {
      intValue = null;
    } else {
      intValue = Integer.valueOf(infoStrValue);
    }
    return intValue;
  }

  public static Double getInfoAsDouble(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    return getInfoValueAsDouble(value);
  }

  public static List<Double> getInfoAsDoubleList(VariantContext variantContext, Field field) {
    List<Double> doubleValues;

    Object value = variantContext.getAttribute(field.getId());
    if (value == null) {
      doubleValues = List.of();
    } else if (value instanceof List<?> objectValues) {
      int size = objectValues.size();
      if (size == 0) {
        doubleValues = emptyList();
      } else if (size == 1) {
        doubleValues = singletonList(getInfoValueAsDouble(objectValues.get(0)));
      } else {
        doubleValues = new ArrayList<>(objectValues.size());
        for (Object objValue : objectValues) {
          doubleValues.add(getInfoValueAsDouble(objValue));
        }
      }
    } else if (value instanceof String string) {
      doubleValues = singletonList(getInfoStringValueAsDouble(string));
    } else {
      throw new TypeConversionException(value, Double.class);
    }

    return doubleValues;
  }

  private static @Nullable
  Double getInfoValueAsDouble(@Nullable Object objValue) {
    Double doubleValue;
    if (objValue == null) {
      doubleValue = null;
    } else if (objValue instanceof Double doubleVal) {
      doubleValue = doubleVal;
    } else if (objValue instanceof String string) {
      doubleValue = getInfoStringValueAsDouble(string);
    } else {
      throw new TypeConversionException(objValue, Double.class);
    }
    return doubleValue;
  }

  private static @Nullable
  Double getInfoStringValueAsDouble(String infoStrValue) {
    Double doubleValue;
    if (infoStrValue.equals(VCFConstants.MISSING_VALUE_v4)) {
      doubleValue = null;
    } else {
      doubleValue = Double.valueOf(infoStrValue);
    }
    return doubleValue;
  }

  public static String getInfoAsString(VariantContext variantContext, Field field) {
    Object value = variantContext.getAttribute(field.getId());
    return getInfoValueAsString(value);
  }

  public static List<String> getInfoAsStringList(VariantContext variantContext, Field field) {
    List<String> strValues;
    String id = field.getId();

    strValues = getInfoAsStringList(variantContext, id);

    return strValues;
  }

  public static List<String> getInfoAsStringList(VariantContext variantContext, String id) {
    List<String> strValues;
    Object value = variantContext.getAttribute(id);
    if (value == null) {
      strValues = List.of();
    } else if (value instanceof List<?> objectValues) {
      int size = objectValues.size();
      if (size == 0) {
        strValues = emptyList();
      } else if (size == 1) {
        strValues = singletonList(getInfoValueAsString(objectValues.get(0)));
      } else {
        strValues = new ArrayList<>(objectValues.size());
        for (Object objValue : objectValues) {
          strValues.add(getInfoValueAsString(objValue));
        }
      }
    } else if (value instanceof String string) {
      strValues = singletonList(getInfoStringValueAsString(string));
    } else {
      throw new TypeConversionException(value, String.class);
    }

    return strValues;
  }

  private static @Nullable
  String getInfoValueAsString(@Nullable Object objValue) {
    String strValue;
    if (objValue == null) {
      strValue = null;
    } else if (objValue instanceof String string) {
      strValue = getInfoStringValueAsString(string);
    } else {
      throw new TypeConversionException(objValue, String.class);
    }
    return strValue;
  }

  private static @Nullable
  String getInfoStringValueAsString(String infoStrValue) {
    String stringValue;
    if (infoStrValue.equals(VCFConstants.MISSING_VALUE_v4)) {
      stringValue = null;
    } else {
      stringValue = infoStrValue;
    }
    return stringValue;
  }

  public static boolean getInfoAsBoolean(VariantContext variantContext, Field field) {
    Object objValue = variantContext.getAttribute(field.getId());
    return getInfoValueAsBoolean(objValue);
  }

  private static boolean getInfoValueAsBoolean(Object objValue) {
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

  public static Object getTypedInfoValue(Field field, String stringValue, String separator) {
    Object value;
    if(separator == null){
      value = getTypedInfoValue(field, stringValue);
    }else{
      List<String> values = Arrays.asList(stringValue.split(separator));
      value = values.stream().map(singleValue -> getTypedInfoValue(field, singleValue)).toList();
    }
    return value;
  }

    public static Object getTypedInfoValue(Field field, String stringValue) {
      Object typedValue;
      ValueType valueType = field.getValueType();
      switch (valueType) {
        case INTEGER:
          typedValue = VcfUtils.getInfoValueAsInteger(stringValue);
          break;
        case FLAG:
          typedValue = VcfUtils.getInfoValueAsBoolean(stringValue);
          break;
        case FLOAT:
          typedValue = VcfUtils.getInfoValueAsDouble(stringValue);
          break;
        case CHARACTER, STRING:
          typedValue = VcfUtils.getInfoValueAsString(stringValue);
          break;
        default:
          throw new UnexpectedEnumException(valueType);
      }
      return typedValue;
    }

  public static VcfRecord createEmptyCsqRecord(VcfRecord vcfRecord, VepHeaderLine vepMetadata,
      Integer alleleIndex) {
    Map<String, NestedField> fields = vepMetadata.getNestedFields();
    List<String> values = new ArrayList<>();
    for (int index = 0; index < fields.size(); index++) {
      values.add("");
    }
    values.add(fields.get(ALLELE_NUM).getIndex(), alleleIndex.toString());
    VariantContext variantContext = vcfRecord.getVariantContext();
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(vepMetadata.getParentField().getId(),
        singletonList(Strings.join(values, '|')));
    return new VcfRecord(variantContextBuilder.make());
  }
}
