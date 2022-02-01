package org.molgenis.vcf.decisiontree.filter;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoMetadataMapper.ALLELE_NUM;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.createEmptyCsqRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.runner.info.VepHeaderLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifierImpl implements Classifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierImpl.class);

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

  public Map<Integer, List<VcfRecord>> getRecordPerConsequence(VcfRecord vcfRecord,
      VepHeaderLine vepHeaderLine) {
    List<String> consequences = vcfRecord.getVepValues(vepHeaderLine.getParentField());
    Map<Integer, List<VcfRecord>> records = new HashMap<>();
    for (String consequence : consequences) {
      int alleleNumIndex;
      if (vepHeaderLine.getField(ALLELE_NUM) instanceof NestedField alleleField) {
        alleleNumIndex = alleleField.getIndex();
      } else {
        throw new UnknownFieldException(ALLELE_NUM, FieldType.INFO_VEP);
      }
      int index = Integer.parseInt(consequence.split("\\|")[alleleNumIndex]);
      List<VcfRecord> csqs;
      if (records.containsKey(index)) {
        csqs = records.get(index);
      } else {
        csqs = new ArrayList<>();
      }
      csqs.add(vcfRecord.getFilteredCopy(consequence, vepHeaderLine.getParentField()));
      records.put(index, csqs);
    }
    return records;
  }

  private VcfRecord processRecord(
      VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata,
      ConsequenceAnnotator consequenceAnnotator) {

    VepHeaderLine vepMetadata = vcfMetadata.getVepHeaderLine();
    Map<Integer, List<VcfRecord>> alleleCsqMap = getRecordPerConsequence(vcfRecord,
        vepMetadata);
    List<String> annotatedCsqs = new ArrayList<>();
    for (int alleleIndex = 0; alleleIndex < vcfRecord.getNrAltAlleles(); alleleIndex++) {
      Integer vepAlleleIndex = alleleIndex + 1;
      Allele allele = vcfRecord.getAltAllele(alleleIndex);
      List<VcfRecord> singleCsqRecords = alleleCsqMap.get(vepAlleleIndex);
      if (singleCsqRecords == null || singleCsqRecords.isEmpty()) {
        singleCsqRecords = singletonList(
            createEmptyCsqRecord(vcfRecord, vepMetadata, vepAlleleIndex));
      }
      for (VcfRecord singleCsqRecord : singleCsqRecords) {
        Variant variant = new Variant(vcfMetadata, singleCsqRecord, allele);
        Decision decision = decisionTreeExecutor.execute(decisionTree, variant);
        String csqString = singleCsqRecord.getVepValues(vepMetadata.getParentField())
            .get(0);
        annotatedCsqs.add(consequenceAnnotator.annotate(decision, csqString));
      }
    }
    vcfRecord.setAttribute(vepMetadata.getParentField(), annotatedCsqs);

    return vcfRecord;
  }
}
