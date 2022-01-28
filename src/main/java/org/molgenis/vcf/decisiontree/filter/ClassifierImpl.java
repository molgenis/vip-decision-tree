package org.molgenis.vcf.decisiontree.filter;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.ALLELE_NUM;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.runner.info.NestedInfoHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VcfNestedMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifierImpl implements Classifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierImpl.class);
  public static final String CSQ = "CSQ";

  private final DecisionTreeExecutor decisionTreeExecutor;

  public ClassifierImpl(DecisionTreeExecutor decisionTreeExecutor) {
    this.decisionTreeExecutor = requireNonNull(decisionTreeExecutor);
  }

  @Override
  public void classify(VcfReader vcfReader, DecisionTree decisionTree,
      RecordWriter recordWriter, ConsequenceAnnotator consequenceAnnotator) {
    VcfMetadata vcfMetadata = vcfReader.getMetadata();

    AtomicInteger nrRecord = new AtomicInteger(0);
    vcfReader.stream()
        .map(
            vcfRecord -> processRecord(vcfRecord, decisionTree,
                vcfMetadata, consequenceAnnotator)).forEach(vcfRecord -> {
          recordWriter.write(vcfRecord);
          if (nrRecord.incrementAndGet() % 25000 == 0) {
            LOGGER.debug("processed {} records", nrRecord);
          }
        });
  }

  public Map<Integer, List<VcfRecord>> getRecordPerConsequence(VcfRecord record,
      NestedInfoHeaderLine vepMetadata) {
    VariantContext variantContext = record.getVariantContext();
    List<String> consequences = variantContext.getAttributeAsStringList(CSQ, "");
    Map<Integer, List<VcfRecord>> records = new HashMap<>();
    for (String consequence : consequences) {
      VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
      variantContextBuilder.attribute(CSQ, singletonList(consequence));
      VariantContext filterVariantContext = variantContextBuilder.make();
      Integer alleleNumIndex = vepMetadata.getField(ALLELE_NUM).getIndex();
      int index = Integer.parseInt(consequence.split("\\|")[alleleNumIndex]) - 1;
      List<VcfRecord> csqs;
      if (records.containsKey(index)) {
        csqs = records.get(index);
      } else {
        csqs = new ArrayList<>();
      }
      csqs.add(new VcfRecord(filterVariantContext));
      records.put(index, csqs);
    }
    return records;
  }

  private VcfRecord processRecord(
      VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata,
      ConsequenceAnnotator consequenceAnnotator) {

    VcfNestedMetadata nestedMetadata = vcfMetadata.getNestedMetadata();
    NestedInfoHeaderLine vepMetadata = nestedMetadata.getNestedLines()
        .get(CSQ);
    Map<Integer, List<VcfRecord>> alleleCsqMap = getRecordPerConsequence(vcfRecord,
        vepMetadata);
    List<String> annotatedCsqs = new ArrayList<>();
    for (int alleleIndex = 0; alleleIndex < vcfRecord.getNrAltAlleles(); alleleIndex++) {
      Allele allele = vcfRecord.getAltAllele(alleleIndex);
      List<VcfRecord> singleCsqRecords = alleleCsqMap.get(Integer.valueOf(alleleIndex));
      if (singleCsqRecords == null || singleCsqRecords.isEmpty()) {
        singleCsqRecords = singletonList(createEmptyCsqRecord(vcfRecord, vepMetadata, alleleIndex));
      }
      for (VcfRecord singleCsqRecord : singleCsqRecords) {
        Variant variant = new Variant(vcfMetadata, singleCsqRecord, allele);
        Decision decision = decisionTreeExecutor.execute(decisionTree, variant);
        String csqString = singleCsqRecord.getVariantContext().getAttributeAsStringList(CSQ, "")
            .get(0);
        annotatedCsqs.add(consequenceAnnotator.annotate(decision, csqString));
      }
    }
    vcfRecord.setAttribute("CSQ", annotatedCsqs);

    return vcfRecord;
  }

  private VcfRecord createEmptyCsqRecord(VcfRecord vcfRecord, NestedInfoHeaderLine vepMetadata,
      Integer alleleIndex) {
    Map<String, NestedField> fields = vepMetadata.getNestedFields();
    List<String> values = new ArrayList<>();
    for (int index = 0; index < fields.size(); index++) {
      values.add("");
    }
    values.add(fields.get(ALLELE_NUM).getIndex(), alleleIndex.toString());
    VariantContext variantContext = vcfRecord.getVariantContext();
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(CSQ, singletonList(Strings.join(values, '|')));
    return new VcfRecord(variantContextBuilder.make());
  }
}
