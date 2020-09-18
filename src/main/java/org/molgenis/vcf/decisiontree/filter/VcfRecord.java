package org.molgenis.vcf.decisiontree.filter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNestedField;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNestedMetadata;
import org.molgenis.vcf.decisiontree.loader.model.ConfigSelector;
import org.molgenis.vcf.decisiontree.utils.VcfUtils;

/**
 * {@link VariantContext} wrapper that works with nested data (e.g. CSQ INFO fields)..
 */
public class VcfRecord {

  private final VariantContext variantContext;
  private final Map<String, ConfigNestedMetadata> nestedMetadata;


  public VcfRecord(VariantContext variantContext,
      Map<String, ConfigNestedMetadata> nestedMetadata) {
    this.variantContext = requireNonNull(variantContext);
    this.nestedMetadata = nestedMetadata;
  }

  public int getNrAltAlleles() {
    return variantContext.getNAlleles() - 1;
  }

  public String getAltAllele(int altAlleleIndex) {
    return variantContext.getAlternateAllele(altAlleleIndex).getBaseString();
  }

  public Object getValue(Field field, int alleleIndex) {
    Object value;
    FieldType fieldType = field.getFieldType();
    switch (fieldType) {
      case COMMON:
        value = getCommonValue(field, alleleIndex);
        break;
      case INFO:
        value = getInfoValue(field, alleleIndex);
        break;
      case INFO_NESTED:
        String[] fieldTokens = field.getId().split("/");
        List<String> listValue = VcfUtils.getInfoAsStringList(variantContext, fieldTokens[0]);
        ConfigNestedMetadata meta = nestedMetadata.get(fieldTokens[0]);
        value = null;
        for (String singleValue : listValue) {
            ConfigNestedField subFieldMeta = meta
                .getFields().get(fieldTokens[1]);
          if (selectorsPass(meta,singleValue, alleleIndex)) {
            int index = subFieldMeta.getIndex();
            //FIXME pipe separator from config
            String singleSubValue = singleValue.split("\\|")[index];
            switch (subFieldMeta.getNumber()) {
              case A:
              case R:
                throw new UnsupportedOperationException(
                    subFieldMeta.getNumber() + "within a nested value???");//FIXME
              case VARIABLE:
                value = Arrays.asList(singleSubValue.split(subFieldMeta.getSeparator()));
                break;
              case FIXED:
               value = subFieldMeta.getCount() == 1 ? singleValue
                    : Arrays.asList(singleSubValue.split(subFieldMeta.getSeparator()));
                break;
              default:
                throw new UnexpectedEnumException(subFieldMeta.getNumber());
            }
          }
        }
        break;
      case FORMAT:
        throw new UnsupportedOperationException("FORMAT values are not yet supported."); // TODO
      default:
        throw new UnexpectedEnumException(fieldType);
    }
    return value;
  }

  private boolean selectorsPass(ConfigNestedMetadata meta, String nestedValueString, int alleleIndex) {
    for(ConfigSelector selector : meta.getUnique()){
      ConfigNestedField field = meta.getFields().get(selector.getField());
      Object value = selector.getValue();
      if(value.equals("SELECTED_ALLELE")){
        value = variantContext.getAlternateAllele(alleleIndex-1).getBaseString();
      }
      //FIXME: use separator, but cope with pipes
      String nestedValue = nestedValueString.split("\\|")[field.getIndex()];
      if(!nestedValue.equals(value)){
        return false;
      }
    }
    return true;
  }

  private Object getCommonValue(Field field, int alleleIndex) {
    Object value;
    switch (field.getId()) {
      case "#CHROM":
        value = variantContext.getContig();
        break;
      case "POS":
        value = variantContext.getStart();
        break;
      case "ID":
        value = variantContext.hasID() ? asList(variantContext.getID().split(";")) : emptyList();
        break;
      case "REF":
        value = variantContext.getReference().getBaseString();
        break;
      case "ALT":
        value = getAltAllele(alleleIndex - 1);
        break;
      case "QUAL":
        value = variantContext.hasLog10PError() ? variantContext.getPhredScaledQual() : null;
        break;
      case "FILTER":
        value = variantContext.getFilters();
        break;
      default:
        throw new UnknownFieldException(field.getId(), FieldType.COMMON);
    }
    return value;
  }

  private Object getInfoValue(Field field, int alleleIndex) {
    Object value;

    ValueCount valueCount = field.getValueCount();
    Type valueCountType = valueCount.getType();
    switch (valueCountType) {
      case A:
        List<?> aInfoList = getInfoList(field);
        value = !aInfoList.isEmpty() ? aInfoList.get(alleleIndex - 1) : null;
        break;
      case R:
        List<?> rInfoList = getInfoList(field);
        value = !rInfoList.isEmpty() ? rInfoList.get(alleleIndex) : null;
        break;
      case VARIABLE:
        value = getInfoList(field);
        break;
      case FIXED:
        value = valueCount.getCount() == 1 ? getInfo(field) : getInfoList(field);
        break;
      default:
        throw new UnexpectedEnumException(valueCountType);
    }
    return value;
  }

  private Object getInfo(Field field) {
    Object value;
    ValueType valueType = field.getValueType();
    switch (valueType) {
      case INTEGER:
        value = VcfUtils.getInfoAsInteger(variantContext, field);
        break;
      case FLAG:
        value = VcfUtils.getInfoAsBoolean(variantContext, field);
        break;
      case FLOAT:
        value = VcfUtils.getInfoAsDouble(variantContext, field);
        break;
      case CHARACTER:
      case STRING:
        value = VcfUtils.getInfoAsString(variantContext, field);
        break;
      default:
        throw new UnexpectedEnumException(valueType);
    }
    return value;
  }

  private List<?> getInfoList(Field field) {
    List<?> listValues;
    ValueType valueType = field.getValueType();
    switch (valueType) {
      case INTEGER:
        listValues = VcfUtils.getInfoAsIntegerList(variantContext, field);
        break;
      case FLOAT:
        listValues = VcfUtils.getInfoAsDoubleList(variantContext, field);
        break;
      case CHARACTER:
      case STRING:
        listValues = VcfUtils.getInfoAsStringList(variantContext, field);
        break;
      case FLAG:
        throw new FlagListException(field.getId());
      default:
        throw new UnexpectedEnumException(valueType);
    }
    return listValues;
  }

  public VariantContext unwrap() {
    return variantContext;
  }

  public String toDisplayString() {
    return String.format(
        "%s:%s %s",
        variantContext.getContig(),
        variantContext.getStart(),
        variantContext.getReference().getBaseString());
  }
}
