package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.decisiontree.runner.ScoreCalculator;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotateScoreImpl implements Classifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotateScoreImpl.class);

  private final VepHelper vepHelper;
  private final VcfMetadata vcfMetadata;
  private final RecordWriter recordWriter;
  private final VipScoreAnnotator vipScoreAnnotator;

  public AnnotateScoreImpl(VepHelper vepHelper, RecordWriter recordWriter, VcfMetadata vcfMetadata, VipScoreAnnotator vipScoreAnnotator) {
    this.vepHelper = requireNonNull(vepHelper);
    this.recordWriter = requireNonNull(recordWriter);
    this.vcfMetadata = requireNonNull(vcfMetadata);
    this.vipScoreAnnotator = vipScoreAnnotator;
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
//    VCFHeader vcfHeader = new VCFHeader(vcfMetadata.unwrap());
//    vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(
//            "VIPVaranScore",
//            VCFHeaderLineCount.UNBOUNDED,
//            VCFHeaderLineType.Integer, "VIP-Varan_score"));
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
        String csqString = singleCsqRecord.getVepValues(nestedHeaderLine.getParentField())
            .get(0);

        int csqStringLength = csqString.split("\\|").length;
        String constraint = (String) getCustomValue(vcfRecord, allele, "constraint", csqStringLength - 15);
        String region = (String) getCustomValue(vcfRecord, allele, "region", csqStringLength - 14);
        String fathmm = (String) getCustomValue(vcfRecord, allele, "fathmm_fathmm", csqStringLength - 12);
        String ncER = (String) getCustomValue(vcfRecord, allele, "ncER", csqStringLength - 11);
        String reMM = (String) getCustomValue(vcfRecord, allele, "ReMM", csqStringLength - 10);
        String phenotype = (String) getCustomValue(vcfRecord, allele, "phenotype", csqStringLength - 9);
        int vIPVaranScore = ScoreCalculator.calculateScore(region, ncER, fathmm, reMM, constraint);

        annotatedCsqs.add(vipScoreAnnotator.annotate(vIPVaranScore, csqString));
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
    switch (id) {
      case "fathmm_fathmm", "ReMM", "ncER", "constraint" -> {
        Object score = vcfRecord.getValue(nestedField, allele);
        return Objects.requireNonNullElse(score, "0.000");
      }
      case "region", "phenotype" -> {
        if (vcfRecord.getValue(nestedField, allele) == null) {
          return "";
        } else {
          return vcfRecord.getValue(nestedField, allele);
        }
      }
    }
    return "";
  }
}
