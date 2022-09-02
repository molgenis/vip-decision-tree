package org.molgenis.vcf.decisiontree.runner;

import static java.util.Collections.singletonList;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoMetadataMapper.ALLELE_NUM;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.molgenis.vcf.decisiontree.filter.UnknownFieldException;
import org.molgenis.vcf.decisiontree.filter.VcfRecord;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;

public class VepHelper {

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
    VariantContext variantContext = vcfRecord.getVariantContext();
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(nestedHeaderLine.getParentField().getId(),
        singletonList(Strings.join(values, '|')));
    return new VcfRecord(variantContextBuilder.make());
  }
}
