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

  private final VepHelper vepHelper;
  private final VcfMetadata vcfMetadata;
  private final RecordWriter recordWriter;
  private final VipScoreAnnotator vipScoreAnnotator;

  public ClassifierImpl(VepHelper vepHelper, RecordWriter recordWriter, VcfMetadata vcfMetadata) {
    this.vepHelper = requireNonNull(vepHelper);
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
        String csqString = singleCsqRecord.getVepValues(nestedHeaderLine.getParentField())
            .get(0);
        System.out.println(csqString);
        System.out.println(csqString.split("\\|").length);
        double constraint = (double) getCustomValue(vcfRecord, allele, "constraint", 64);
        String region = (String) getCustomValue(vcfRecord, allele, "region", 65);
        double fathmm = (double) getCustomValue(vcfRecord, allele, "fathmm_fathmm", 67);
        double ncER = (double) getCustomValue(vcfRecord, allele, "ncER", 68);
        double reMM = (double) getCustomValue(vcfRecord, allele, "ReMM", 69);
        String phenotype = (String) getCustomValue(vcfRecord, allele, "phenotype", 70);
        int VIPVaranScore = calculateScore(region, ncER, fathmm, reMM, constraint);
        System.out.println("constraint: " + constraint);
        System.out.println("region: " + region);
        System.out.println("fathmm: " +fathmm);
        System.out.println("ncer: " + ncER);
        System.out.println("ReMM: " +reMM);
        System.out.println("phenotype: " + phenotype);

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
    System.out.println(id);
    switch (id) {
      case "fathmm_fathmm", "ReMM", "ncER", "constraint" -> {
        Object score = vcfRecord.getValue(nestedField, allele);
        if (score == null) {
          System.out.println("hier in score");
          return 0.000;
        } else {
          System.out.println("daar in scor");
          return Double.parseDouble((String) score);
        }
      }
      case "region", "phenotype" -> {
        if (vcfRecord.getValue(nestedField, allele) == null) {
          System.out.println("hier in regio");
          return "";
        } else {
          System.out.println("daar in regio");
          return vcfRecord.getValue(nestedField, allele);
        }
      }
    }
    return "";
  }

  private int calculateScore(String region, double ncER, double fathmm, double reMM, double constraint) {
    // level 1: overlap with a region
    // level 2: level 1 + score of ncER(>0.499) fathmm(>0.5) ReMM*(>0.5)
    // level 3: level 2 + constraint region above or equal to 0.7
    // level 4: Gado phenotype (something with phenotype)
    int vipVaranScore  = 0;
    if (!region.isEmpty()) {
      vipVaranScore = 1;
    }
    if (!region.isEmpty() && ncER > 49 || fathmm > 0.5 || reMM > 0.5) {
      vipVaranScore = 2;
    }
    if (!region.isEmpty() && ncER > 49 || fathmm > 0.5 || reMM > 0.5 & constraint >= 0.7) {
      vipVaranScore = 3;
    }

    return vipVaranScore;
  }
}
