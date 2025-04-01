package org.molgenis.vcf.decisiontree.runner;

import static java.util.Collections.singletonList;
import static org.molgenis.vcf.decisiontree.runner.info.VepMetadataMapperImpl.*;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.apache.logging.log4j.util.Strings;
import org.molgenis.vcf.decisiontree.filter.UnknownFieldException;
import org.molgenis.vcf.decisiontree.filter.VcfRecord;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;

public class VepHelper {
  public static final String INFO_DESCRIPTION_PREFIX = "Consequence annotations from Ensembl VEP. Format: ";

  public Map<Integer, List<VcfRecord>> getRecordPerConsequence(VcfRecord vcfRecord,
      NestedHeaderLine nestedHeaderLine) {
    List<String> consequences = vcfRecord.getVepValues(nestedHeaderLine.getParentField());
    Map<Integer, List<VcfRecord>> records = new HashMap<>();
    for (String consequence : consequences) {
      int alleleNumIndex;
      if (nestedHeaderLine.getField(ALLELE_NUM) instanceof NestedField alleleField) {
        alleleNumIndex = alleleField.getIndex();
      } else {
        throw new UnknownFieldException(ALLELE_NUM, FieldType.INFO_VEP);
      }
      int index = Integer.parseInt(consequence.split("\\|")[alleleNumIndex]);
      List<VcfRecord> singleCsqRecord;
      if (records.containsKey(index)) {
        singleCsqRecord = records.get(index);
      } else {
        singleCsqRecord = new ArrayList<>();
      }
      singleCsqRecord.add(
          vcfRecord.getFilteredCopy(consequence, nestedHeaderLine.getParentField()));
      records.put(index, singleCsqRecord);
    }
    return records;
  }


  public VcfRecord createEmptyCsqRecord(VcfRecord vcfRecord,
      Integer alleleIndex, NestedHeaderLine nestedHeaderLine) {
    Map<String, NestedField> fields = nestedHeaderLine.getNestedFields();
    List<String> values = new ArrayList<>();
    for (int index = 0; index < fields.size(); index++) {
      values.add("");
    }
    values.set(fields.get(ALLELE_NUM).getIndex(), alleleIndex.toString());
    NestedField pick = fields.get(PICK);
    if(pick != null) {
      values.set(pick.getIndex(), "1");
    }
    VariantContext variantContext = vcfRecord.getVariantContext();
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(nestedHeaderLine.getParentField().getId(),
        singletonList(Strings.join(values, '|')));
    return new VcfRecord(variantContextBuilder.make());
  }

  public static String getVepId(VCFHeader vcfHeader) {
    for(VCFInfoHeaderLine vcfInfoHeaderLine : vcfHeader.getInfoHeaderLines()){
      if(canMap(vcfInfoHeaderLine)){
        return vcfInfoHeaderLine.getID();
      }
    }
    return null;
  }

  public static boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    // match on the description since the INFO ID is configurable (default: CSQ)
    String description = vcfInfoHeaderLine.getDescription();
    return description.startsWith(INFO_DESCRIPTION_PREFIX);
  }
}
