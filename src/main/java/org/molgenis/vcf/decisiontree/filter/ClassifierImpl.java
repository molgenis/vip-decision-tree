package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import htsjdk.variant.vcf.*;
import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
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
  private final VipScoreAnnotator vipScoreAnnotator;

  public ClassifierImpl(DecisionTreeExecutor decisionTreeExecutor, VepHelper vepHelper,
      DecisionTree decisionTree, ConsequenceAnnotator consequenceAnnotator,
      RecordWriter recordWriter, VcfMetadata vcfMetadata) {
    this.decisionTreeExecutor = requireNonNull(decisionTreeExecutor);
    this.vepHelper = requireNonNull(vepHelper);
    this.decisionTree = requireNonNull(decisionTree);
    this.consequenceAnnotator = requireNonNull(consequenceAnnotator);
    this.recordWriter = requireNonNull(recordWriter);
    this.vcfMetadata = requireNonNull(vcfMetadata);
    this.vipScoreAnnotator = new VipScoreAnnotator(false, false);
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
    NestedHeaderLine nestedHeaderLine = vcfMetadata.getVepHeaderLine();
    Map<Integer, List<VcfRecord>> alleleCsqMap = vepHelper.getRecordPerConsequence(vcfRecord,
        nestedHeaderLine);
    List<String> annotatedCsqs = new ArrayList<>();
    for (int alleleIndex = 0; alleleIndex < vcfRecord.getNrAltAlleles(); alleleIndex++) {
      Integer vepAlleleIndex = alleleIndex + 1;
      Allele allele = vcfRecord.getAltAllele(alleleIndex);
      List<VcfRecord> singleCsqRecords = alleleCsqMap.get(vepAlleleIndex);
      if (singleCsqRecords == null || singleCsqRecords.isEmpty()) {
        singleCsqRecords = List.of(
            vepHelper.createEmptyCsqRecord(vcfRecord, vepAlleleIndex, nestedHeaderLine));
      }
      for (VcfRecord singleCsqRecord : singleCsqRecords) {
        Variant variant = new Variant(vcfMetadata, singleCsqRecord, allele);
        Decision decision = decisionTreeExecutor.execute(decisionTree, variant);
        String csqString = singleCsqRecord.getVepValues(nestedHeaderLine.getParentField())
            .get(0);
//        annotatedCsqs.add(consequenceAnnotator.annotate(decision, csqString));

        float constraint = Float.parseFloat((String) getCustomValue(vcfRecord, allele, "constraint", 64));
        String region = (String) getCustomValue(vcfRecord, allele, "region", 65);
        float fathmm = Float.parseFloat((String) getCustomValue(vcfRecord, allele, "fathmm_fathmm", 67));
        float ncER = Float.parseFloat((String) getCustomValue(vcfRecord, allele, "ncER", 68));
        float ReMM = Float.parseFloat((String) getCustomValue(vcfRecord, allele, "ReMM", 69));
        String phenotype = (String) getCustomValue(vcfRecord, allele, "phenotype", 70);
        int VIPVaranScore = 999991;
        System.out.println(constraint);
        System.out.printf(region);
        System.out.println("---");
        System.out.printf(String.valueOf(fathmm));
        System.out.println("---");
        System.out.printf(String.valueOf(ncER));
        System.out.println("---");
        System.out.printf(String.valueOf(ReMM));
        System.out.println("---");
        System.out.printf(phenotype);

        VCFHeader vcfHeader = new VCFHeader(vcfMetadata.unwrap());
        vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(
                "VIPVaranScore",
                VCFHeaderLineCount.UNBOUNDED,
                VCFHeaderLineType.Integer, "VIP-Varan score"));
        annotatedCsqs.add(vipScoreAnnotator.annotate(VIPVaranScore, csqString));
      }
    }
    vcfRecord.setAttribute(nestedHeaderLine.getParentField(), annotatedCsqs);

    return vcfRecord;
  }

  private Object getCustomValue(VcfRecord vcfRecord, Allele allele, String id, int index) {
    ValueCount valueCount = ValueCount.builder().type(Type.VARIABLE).build();
    FieldImpl parent =
            FieldImpl.builder()
                    .id("CSQ")
                    .fieldType(FieldType.INFO)
                    .valueType(ValueType.STRING)
                    .valueCount(valueCount)
                    .separator('|')
                    .build();
    NestedField nestedField = NestedField.nestedBuilder().id(id).parent(parent)
            .fieldType(FieldType.INFO_VEP).index(index)
            .valueType(ValueType.STRING).valueCount(valueCount).build();

    if (vcfRecord.getValue(nestedField, allele) != null) {
      return vcfRecord.getValue(nestedField, allele);
    } else {
      return "0.000";
    }
  }
}
