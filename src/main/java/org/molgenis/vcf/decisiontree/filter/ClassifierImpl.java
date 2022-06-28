package org.molgenis.vcf.decisiontree.filter;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.VepHeaderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifierImpl implements Classifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierImpl.class);

  private final DecisionTreeExecutor decisionTreeExecutor;
  private final VepHelper vepHelper;
  private final DecisionTree decisionTree;
  private final VcfMetadata vcfMetadata;
  private final ConsequenceAnnotator consequenceAnnotator;
  private final RecordWriter recordWriter;

  public ClassifierImpl(DecisionTreeExecutor decisionTreeExecutor, VepHelper vepHelper,
      DecisionTree decisionTree, ConsequenceAnnotator consequenceAnnotator,
      RecordWriter recordWriter, VcfMetadata vcfMetadata) {
    this.decisionTreeExecutor = requireNonNull(decisionTreeExecutor);
    this.vepHelper = requireNonNull(vepHelper);
    this.decisionTree = decisionTree;
    this.consequenceAnnotator = consequenceAnnotator;
    this.recordWriter = recordWriter;
    this.vcfMetadata = vcfMetadata;
  }

  @Override
  public void classify(VcfReader vcfReader) {
    AtomicInteger nrRecord = new AtomicInteger(0);
    vcfReader.stream()
        .map(
            this::processRecord).forEach(vcfRecord -> {
          recordWriter.write(vcfRecord);
          if (nrRecord.incrementAndGet() % 25000 == 0) {
            LOGGER.debug("processed {} records", nrRecord);
          }
        });
  }

  private VcfRecord processRecord(
      VcfRecord vcfRecord) {
    VepHeaderLine vepHeaderLine = vcfMetadata.getVepHeaderLine();
    Map<Integer, List<VcfRecord>> alleleCsqMap = vepHelper.getRecordPerConsequence(vcfRecord,
        vepHeaderLine);
    List<String> annotatedCsqs = new ArrayList<>();
    for (int alleleIndex = 0; alleleIndex < vcfRecord.getNrAltAlleles(); alleleIndex++) {
      Integer vepAlleleIndex = alleleIndex + 1;
      Allele allele = vcfRecord.getAltAllele(alleleIndex);
      List<VcfRecord> singleCsqRecords = alleleCsqMap.get(vepAlleleIndex);
      if (singleCsqRecords == null || singleCsqRecords.isEmpty()) {
        singleCsqRecords = singletonList(
            vepHelper.createEmptyCsqRecord(vcfRecord, vepAlleleIndex, vepHeaderLine));
      }
      for (VcfRecord singleCsqRecord : singleCsqRecords) {
        Variant variant = new Variant(vcfMetadata, singleCsqRecord, allele);
        Decision decision = decisionTreeExecutor.execute(decisionTree, variant);
        String csqString = singleCsqRecord.getVepValues(vepHeaderLine.getParentField())
            .get(0);
        annotatedCsqs.add(consequenceAnnotator.annotate(decision, csqString));
      }
    }
    vcfRecord.setAttribute(vepHeaderLine.getParentField(), annotatedCsqs);

    return vcfRecord;
  }
}
