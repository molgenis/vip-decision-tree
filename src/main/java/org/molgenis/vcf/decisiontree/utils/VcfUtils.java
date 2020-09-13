package org.molgenis.vcf.decisiontree.utils;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.List;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.Variant;

/**
 * TODO refactor getValue(...) methods for more robust code, performance and improved readability:
 * 1. Build decision tree using input VCF metadata 2. Store decision tree node fields as a Field
 * object with Number number / Type type fields 3. Remove Variant.VcfHeader
 */
public class VcfUtils {

  private static final String INFO_PREFIX = "INFO:";
  private static final String SAMPLE_PREFIX = "SAMPLE:";

  private VcfUtils() {}

  public static Object getValue(List<String> fields, Variant rfc) {
    String field = fields.stream().collect(Collectors.joining(":"));
    return getValue(field, rfc);
  }

  public static Object getValue(String field, Variant rfc) {
    VariantContext vc = rfc.getVcfRecord().unwrap();
    if (field.startsWith(INFO_PREFIX)) {
      String infoField = field.replace(INFO_PREFIX, "");
      Object value = getValue(field, vc);
      if (value == null) {
        return null;
      }
      VCFInfoHeaderLine infoHeader = rfc.getVcfMetadata().unwrap().getInfoHeaderLine(infoField);
      if (infoHeader.getCountType() == VCFHeaderLineCount.A) {
        if (vc.getAlternateAlleles().size() > 1) {
          if (value instanceof List) {
            List<Object> values = (List<Object>) value;
            if (vc.getAlternateAlleles().size() == values.size()) {
              Object alleleValue = values.get(rfc.getAlleleIndex() - 1);
              // TODO can we prevent doing missing value handling here?
              return ".".equals(alleleValue) ? null : alleleValue;
            } else {
              throw new IllegalStateException("FIXME: mismatch alleles and values");
            }
          } else {
            throw new IllegalStateException("FIXME: mismatch alleles and values");
          }
        } else if (vc.getAlternateAlleles().size() == 1) {
          if (value instanceof java.awt.List) {
            throw new IllegalStateException("FIXME: mismatch alleles and values");
          }
          return value;
        }
      } else {
        return getValue(field, vc);
      }
    }
    return getValue(field, vc);
  }

  public static Object getValue(List<String> fields, VariantContext vc) {
    String field = fields.stream().collect(Collectors.joining(":"));
    return getValue(field, vc);
  }

  public static Object getValue(String field, VariantContext vc) {
    if (field.startsWith(INFO_PREFIX)) {
      String infoField = field.replace(INFO_PREFIX, "");

      // TODO use the method that matches info metadata, e.g. getAttributeAsStringList
      return vc.getAttribute(infoField);
    }
    if (field.startsWith(SAMPLE_PREFIX)) {
      throw new UnsupportedOperationException("TODO: implement");
    } else {
      switch (field) {
        case "#CHROM":
          return vc.getContig();
        case "POS":
          return vc.getStart();
        case "REF":
          return vc.getReference();
        case "ALT":
          return vc.getAlternateAlleles();
        case "QUAL":
          return vc.getPhredScaledQual(); // FIXME: is this correct?
        case "FILTER":
          return vc.getFiltersMaybeNull(); // FIXME: is this correct?
        default:
          throw new IllegalArgumentException(String.format("Unknown field: %s", field));
      }
    }
  }

  public static String getVariantIdentifier(Variant variant) {
    return variant.getVcfRecord().unwrap().getContig()
        + " "
        + variant.getVcfRecord().unwrap().getStart()
        + " "
        + variant.getVcfRecord().unwrap().getAlternateAllele(variant.getAlleleIndex() - 1);
  }
}
