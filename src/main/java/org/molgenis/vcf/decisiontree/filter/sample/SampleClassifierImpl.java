package org.molgenis.vcf.decisiontree.filter.sample;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.sample.SampleAnnotatorImpl.VISD;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.molgenis.vcf.decisiontree.SampleInfo;
import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.DecisionTreeExecutor;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.Variant;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.filter.VcfRecord;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.SampleMeta;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.VepHeaderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleClassifierImpl implements Classifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(SampleClassifierImpl.class);

  private final DecisionTreeExecutor decisionTreeExecutor;
  private final VepHelper vepHelper;
  private final DecisionTree decisionTree;
  private final RecordWriter recordWriter;
  private final SampleAnnotator sampleAnnotator;
  private final SampleInfo sampleInfo;

  public SampleClassifierImpl(DecisionTreeExecutor decisionTreeExecutor, VepHelper vepHelper,
      DecisionTree decisionTree,
      RecordWriter recordWriter,
      SampleAnnotator sampleAnnotator, SampleInfo sampleInfo) {
    this.decisionTreeExecutor = requireNonNull(decisionTreeExecutor);
    this.vepHelper = requireNonNull(vepHelper);
    this.decisionTree = decisionTree;
    this.recordWriter = recordWriter;
    this.sampleAnnotator = sampleAnnotator;
    this.sampleInfo = sampleInfo;
  }

  @Override
  public void classify(VcfReader vcfReader) {
    VcfMetadata vcfMetadata = vcfReader.getMetadata();

    AtomicInteger nrRecord = new AtomicInteger(0);
    vcfReader.stream()
        .map(
            vcfRecord -> processRecordSample(vcfRecord, decisionTree,
                vcfMetadata, sampleAnnotator, sampleInfo)).forEach(vcfRecord -> {
          recordWriter.write(vcfRecord);
          if (nrRecord.incrementAndGet() % 25000 == 0) {
            LOGGER.debug("processed {} records", nrRecord);
          }
        });
  }

  private VcfRecord processRecordSample(
      VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata,
      SampleAnnotator sampleAnnotator, SampleInfo sampleInfo) {
    VepHeaderLine vepHeaderLine = vcfMetadata.getVepHeaderLine();
    Map<Integer, List<VcfRecord>> alleleCsqMap = vepHelper.getRecordPerConsequence(vcfRecord,
        vepHeaderLine);
    Set<String> decisions = new HashSet<>();
    VariantContext vc = vcfRecord.getVariantContext();
    for (String sampleName : vc.getSampleNames()) {
      List<String> sampleDecisions = new ArrayList<>();
      processRecord(vcfRecord, decisionTree, vcfMetadata, vepHeaderLine, alleleCsqMap,
          getSampleMeta(sampleName, sampleInfo),
          sampleDecisions);
      vc = sampleAnnotator.annotate(String.join(",", sampleDecisions), sampleName, vc);
      decisions.addAll(sampleDecisions);
    }
    VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
    if (!decisions.isEmpty()) {
      vcBuilder.attribute(VISD, String.join(",", decisions));
    }
    return new VcfRecord(vcBuilder.make());
  }

  private SampleMeta getSampleMeta(String sampleName, SampleInfo sampleInfo) {
    Map<String, SampleMeta> sampleMetaMap = sampleInfo.getSampleMetaMap();
    SampleMeta sampleMeta = null;
    if (sampleMetaMap != null) {
      sampleMeta = sampleMetaMap.get(sampleName);
    }
    if (sampleMeta == null) {
      sampleMeta = SampleMeta.builder().samplePhenotypes(sampleInfo.getSamplePhenotypes())
          .sampleName(sampleName).proband(sampleInfo.getProbands().contains(sampleName)).build();
    }
    return sampleMeta;
  }

  private void processRecord
      (VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata,
          VepHeaderLine vepHeaderLine, Map<Integer, List<VcfRecord>> alleleCsqMap,
          SampleMeta sampleMeta, List<String> sampleDecisions) {
    for (int alleleIndex = 0; alleleIndex < vcfRecord.getNrAltAlleles(); alleleIndex++) {
      Integer vepAlleleIndex = alleleIndex + 1;
      Allele allele = vcfRecord.getAltAllele(alleleIndex);
      List<VcfRecord> singleCsqRecords = alleleCsqMap.get(vepAlleleIndex);
      if (singleCsqRecords == null || singleCsqRecords.isEmpty()) {
        singleCsqRecords = singletonList(
            vepHelper.createEmptyCsqRecord(vcfRecord, vepAlleleIndex, vepHeaderLine));
      }
      for (VcfRecord singleCsqRecord : singleCsqRecords) {
        Variant variant = Variant.builder().vcfMetadata(vcfMetadata).vcfRecord(singleCsqRecord)
            .allele(allele).build();
        sampleDecisions.add(
            decisionTreeExecutor.execute(decisionTree, variant, sampleMeta).getClazz());
      }
    }
  }
}
