package org.molgenis.vcf.decisiontree.filter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.getTypedVcfValue;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.GenotypeFieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.utils.VcfUtils;

/**
 * {@link VariantContext} wrapper that works with nested data (e.g. CSQ INFO fields)..
 */
public class VcfRecord {

  private static final List<String> PASS_FILTER = List.of(VCFConstants.PASSES_FILTERS_v4);

  private VariantContext variantContext;

  public VcfRecord(VariantContext variantContext) {
    this.variantContext = requireNonNull(variantContext);
  }

  public VariantContext getVariantContext() {
    return variantContext;
  }

  public int getNrAltAlleles() {
    return variantContext.getNAlleles() - 1;
  }

  public Allele getAltAllele(int altAlleleIndex) {
    String bases = variantContext.getAlternateAllele(altAlleleIndex).getBaseString();
    int alleleIndex = altAlleleIndex + 1;
    return new Allele(bases, alleleIndex);
  }

  public Object getValue(Field field, Allele allele) {
    return getValue(field, allele, null);
  }

  public Object getValue(Field field, Allele allele, SampleContext sampleContext) {
    Object value;
    FieldType fieldType = field.getFieldType();
    switch (fieldType) {
      case COMMON:
        value = getCommonValue(field, allele);
        break;
      case INFO:
        value = getInfoValue(field, allele);
        break;
      case INFO_VEP:
        value = getNestedVepValue(field);
        break;
      case FORMAT_GENOTYPE:
        value = getNestedGTValue((NestedField) field, sampleContext);
        break;
      case FORMAT:
        if (sampleContext == null) {
          throw new UnsupportedOperationException(
              "Cannot filter on FORMAT fields when running in variant filter mode.");
        }
        value = getFormatField(field, sampleContext);
        break;
      case SAMPLE:
        value = getSampleValue(field, sampleContext);
        break;
      default:
        throw new UnexpectedEnumException(fieldType);
    }
    return value;
  }

  private Object getSampleValue(Field field, SampleContext sampleContext) {
    Object value;
    switch (field.getId().toUpperCase()) {
      case "AFFECTED_STATUS":
        value = sampleContext.getAffectedStatus().toString();
        break;
      case "PROBAND":
        value = sampleContext.getProband();
        break;
      case "SEX":
        value = sampleContext.getSex().toString();
        break;
      case "FATHER":
        value = sampleContext.getFather();
        break;
      case "MOTHER":
        value = sampleContext.getMother();
        break;
      case "FAMILY":
        value = sampleContext.getFamily();
        break;
      case "PHENOTYPES":
        value = sampleContext.getPhenotypes();
        break;
      default:
        throw new UnknownFieldException(field.getId(), FieldType.SAMPLE);
    }
    return value;
  }

  private Object getNestedGTValue(NestedField field, SampleContext sampleContext) {
    Genotype genotype = variantContext.getGenotype(sampleContext.getIndex());
    Object value;
    switch (GenotypeFieldType.valueOf(field.getId())) {
      case ALLELES:
        value = genotype.getAlleles().stream().map(
            htsjdk.variant.variantcontext.Allele::getBaseString).toList();
        break;
      case ALLELE_NUM:
        value = genotype.getAlleles().stream().map(allele ->
            variantContext.getAlleles().indexOf(allele)).toList();
        break;
      case TYPE:
        switch (genotype.getType()) {
          case MIXED:
            value = GenotypeType.MIXED.name();
            break;
          case HET:
            value = GenotypeType.HET.name();
            break;
          case HOM_REF:
            value = GenotypeType.HOM_REF.name();
            break;
          case HOM_VAR:
            value = GenotypeType.HOM_VAR.name();
            break;
          case NO_CALL:
            value = GenotypeType.NO_CALL.name();
            break;
          case UNAVAILABLE:
            value = GenotypeType.UNAVAILABLE.name();
            break;
          default:
            throw new UnexpectedEnumException(genotype.getType());
        }
        break;
      case MIXED:
        value = genotype.isMixed();
        break;
      case CALLED:
        value = genotype.isCalled();
        break;
      case PLOIDY:
        value = genotype.getPloidy();
        break;
      case PHASED:
        value = genotype.isPhased();
        break;
      case NON_INFORMATIVE:
        value = genotype.isNonInformative();
        break;
      default:
        throw new UnexpectedEnumException(GenotypeFieldType.valueOf(field.getId()));
    }
    return value;
  }

  private Object getFormatField(Field field, SampleContext sampleContext) {
    Genotype genotype = variantContext.getGenotype(sampleContext.getIndex());
    Object value;
    switch (field.getId()) {
      case ("GT"):
        value = genotype.getGenotypeString();
        break;
      case ("AD"):
        value = IntStream.of(genotype.getAD()).boxed().toList();
        break;
      case ("DP"):
        value = genotype.getDP();
        break;
      case ("GQ"):
        value = genotype.getGQ();
        break;
      case ("PL"):
        value = genotype.getPL();
        break;
      default:
        value = genotype.getExtendedAttribute(field.getId());
        if (value != null) {
          value = VcfUtils.getTypedVcfValue(field, value.toString());
        }
    }
    return value;
  }

  public List<String> getVepValues(Field vepField) {
    return getVariantContext().getAttributeAsStringList(vepField.getId(), "");
  }

  private Object getNestedVepValue(Field field) {
    Object value = null;
    NestedField nestedField = (NestedField) field;
    String separator = Pattern.quote(nestedField.getParent().getSeparator().toString());
    int index = nestedField.getIndex();
    String parentId = nestedField.getParent().getId();
    List<String> infoValues = VcfUtils.getInfoAsStringList(variantContext, parentId);
    if (!infoValues.isEmpty()) {
      String singleValue = infoValues.get(0);
      String[] split = singleValue.split(separator, -1);
      String stringValue = split[index];
      if (!stringValue.isEmpty()) {
        if (field.getSeparator() != null) {
          String nestedSeparator = Pattern.quote(nestedField.getSeparator().toString());
          value = getTypedVcfValue(field, stringValue, nestedSeparator);
        } else {
          value = VcfUtils.getTypedVcfValue(field, stringValue);
        }
      }
    }
    return value;
  }

  private Object getCommonValue(Field field, Allele allele) {
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
        value = allele.getBases();
        break;
      case "QUAL":
        value = variantContext.hasLog10PError() ? variantContext.getPhredScaledQual() : null;
        break;
      case "FILTER":
        value = getCommonFilterValue();
        break;
      default:
        throw new UnknownFieldException(field.getId(), FieldType.COMMON);
    }
    return value;
  }

  private Object getCommonFilterValue() {
    Object value;
    Set<String> filters = variantContext.getFiltersMaybeNull();
    if (filters == null) {
      value = emptyList();
    } else if (filters.isEmpty()) {
      value = PASS_FILTER;
    } else {
      if (filters.size() == 1) {
        value = List.of(filters.iterator().next());
      } else {
        value = new ArrayList<>(filters);
      }
    }
    return value;
  }

  private Object getInfoValue(Field field, Allele allele) {
    Object value;

    ValueCount valueCount = field.getValueCount();
    Type valueCountType = valueCount.getType();
    switch (valueCountType) {
      case A:
        List<?> aInfoList = getInfoList(field);
        value = !aInfoList.isEmpty() ? aInfoList.get(allele.getIndex() - 1) : null;
        break;
      case R:
        List<?> rInfoList = getInfoList(field);
        value = !rInfoList.isEmpty() ? rInfoList.get(allele.getIndex()) : null;
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
      case CHARACTER, STRING:
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
      case CHARACTER, STRING:
        listValues = VcfUtils.getInfoAsStringList(variantContext, field);
        break;
      case FLAG:
        throw new FlagListException(field.getId());
      default:
        throw new UnexpectedEnumException(valueType);
    }
    return listValues;
  }

  public void setAttribute(Field attribute, Object value) {
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(attribute.getId(), value);
    variantContext = variantContextBuilder.make();
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

  public VcfRecord getFilteredCopy(String consequence, Field vepField) {
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(vepField.getId(), List.of(consequence));
    VariantContext filterVariantContext = variantContextBuilder.make();
    return new VcfRecord(filterVariantContext);
  }
}
