package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.VIPC_S;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleClassifierImpl implements Classifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(SampleClassifierImpl.class);

  private final DecisionTreeExecutor decisionTreeExecutor;
  private final VepHelper vepHelper;
  private final DecisionTree decisionTree;
  private final RecordWriter recordWriter;
  private final SampleAnnotator sampleAnnotator;
  private final Set<String> probands;

  public SampleClassifierImpl(DecisionTreeExecutor decisionTreeExecutor, VepHelper vepHelper,
      DecisionTree decisionTree,
      RecordWriter recordWriter, SampleAnnotator sampleAnnotator, Set<String> probands) {
    this.decisionTreeExecutor = requireNonNull(decisionTreeExecutor);
    this.vepHelper = requireNonNull(vepHelper);
    this.decisionTree = requireNonNull(decisionTree);
    this.recordWriter = requireNonNull(recordWriter);
    this.sampleAnnotator = requireNonNull(sampleAnnotator);
    this.probands = requireNonNull(probands);
  }

  @Override
  public void classify(VcfReader vcfReader) {
    VcfMetadata vcfMetadata = vcfReader.getMetadata();

    AtomicInteger nrRecord = new AtomicInteger(0);
    vcfReader.stream()
        .map(
            vcfRecord -> processRecordSample(vcfRecord, decisionTree,
                vcfMetadata, sampleAnnotator)).forEach(vcfRecord -> {
          recordWriter.write(vcfRecord);
          if (nrRecord.incrementAndGet() % 25000 == 0) {
            LOGGER.debug("processed {} records", nrRecord);
          }
        });
  }

  private VcfRecord processRecordSample(
      VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata,
      SampleAnnotator sampleAnnotator) {
    NestedHeaderLine nestedHeaderLine = vcfMetadata.getVepHeaderLine();
    Map<Integer, List<VcfRecord>> alleleCsqMap = vepHelper.getRecordPerConsequence(vcfRecord,
        nestedHeaderLine);
    Set<String> decisions = new LinkedHashSet<>();
    VariantContext vc = vcfRecord.getVariantContext();
    for (int sampleIndex = 0; sampleIndex < vc.getNSamples(); sampleIndex++) {
      String sampleName = vc.getGenotype(sampleIndex).getSampleName();
      if (probands.isEmpty() || probands.contains(sampleName)) {
        List<Decision> sampleDecisions = new ArrayList<>();
        processRecord(vcfRecord, decisionTree, vcfMetadata, nestedHeaderLine, alleleCsqMap,
            sampleIndex,
            sampleDecisions);
        vc = sampleAnnotator.annotate(sampleDecisions, sampleIndex, vc);
        decisions.addAll(
            sampleDecisions.stream().map(Decision::getClazz).toList());
      }
    }
    VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
    if (!decisions.isEmpty()) {
      vcBuilder.attribute(VIPC_S, String.join(",", decisions));
    }
    return new VcfRecord(vcBuilder.make());

  }

  private void processRecord
      (VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata,
          NestedHeaderLine nestedHeaderLine, Map<Integer, List<VcfRecord>> alleleCsqMap,
          Integer sampleIndex,
          List<Decision> sampleDecisions) {
    for (int alleleIndex = 0; alleleIndex < vcfRecord.getNrAltAlleles(); alleleIndex++) {
      Integer vepAlleleIndex = alleleIndex + 1;
      Allele allele = vcfRecord.getAltAllele(alleleIndex);
      List<VcfRecord> singleCsqRecords = alleleCsqMap.get(vepAlleleIndex);
      if (singleCsqRecords == null || singleCsqRecords.isEmpty()) {
        singleCsqRecords = List.of(
            vepHelper.createEmptyCsqRecord(vcfRecord, vepAlleleIndex, nestedHeaderLine));
      }
      for (VcfRecord singleCsqRecord : singleCsqRecords) {
        Variant variant = Variant.builder().vcfMetadata(vcfMetadata).vcfRecord(singleCsqRecord)
            .allele(allele).build();
        sampleDecisions.add(
            decisionTreeExecutor.execute(decisionTree, variant, sampleIndex));
      }
    }
  }
}
