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
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.decisiontree.filter.model.SamplesContext;
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
  private final SamplesContext samplesContext;

  public SampleClassifierImpl(DecisionTreeExecutor decisionTreeExecutor, VepHelper vepHelper,
      DecisionTree decisionTree, RecordWriter recordWriter, SampleAnnotator sampleAnnotator,
      SamplesContext samplesContext) {
    this.decisionTreeExecutor = requireNonNull(decisionTreeExecutor);
    this.vepHelper = requireNonNull(vepHelper);
    this.decisionTree = requireNonNull(decisionTree);
    this.recordWriter = requireNonNull(recordWriter);
    this.sampleAnnotator = requireNonNull(sampleAnnotator);
    this.samplesContext = requireNonNull(samplesContext);
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
    VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
    Set<SampleContext> samplesContexts = samplesContext.getSampleContexts();
    for (SampleContext sampleContext : samplesContexts) {
      if (sampleContext.getProband()) {
        List<Decision> sampleDecisions = new ArrayList<>();
        processRecord(vcfRecord, decisionTree, vcfMetadata, nestedHeaderLine, alleleCsqMap,
            sampleContext, sampleDecisions);
        sampleAnnotator.annotate(sampleDecisions, sampleContext.getIndex(), vcBuilder);
        decisions.addAll(
            sampleDecisions.stream().map(Decision::getClazz).toList());
      }
    }
    if (!decisions.isEmpty()) {
      vcBuilder.attribute(VIPC_S, String.join(",", decisions.stream().sorted().toList()));
    }
    return new VcfRecord(vcBuilder.make());

  }

  private void processRecord
      (VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata,
          NestedHeaderLine nestedHeaderLine, Map<Integer, List<VcfRecord>> alleleCsqMap,
          SampleContext sampleContext,
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
            decisionTreeExecutor.execute(decisionTree, variant, sampleContext));
      }
    }
  }
}
