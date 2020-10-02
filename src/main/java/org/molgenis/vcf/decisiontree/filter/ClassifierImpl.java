package org.molgenis.vcf.decisiontree.filter;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifierImpl implements Classifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierImpl.class);

  private final DecisionTreeExecutor decisionTreeExecutor;

  public ClassifierImpl(DecisionTreeExecutor decisionTreeExecutor) {
    this.decisionTreeExecutor = requireNonNull(decisionTreeExecutor);
  }

  @Override
  public void classify(VcfReader vcfReader, DecisionTree decisionTree, DecisionWriter writer) {
    VcfMetadata vcfMetadata = vcfReader.getMetadata();

    AtomicInteger nrRecord = new AtomicInteger(0);
    vcfReader.stream()
        .forEach(
            vcfRecord -> {
              List<Decision> decisions = processRecord(vcfRecord, decisionTree, vcfMetadata);
              writer.write(decisions, vcfRecord);

              if (nrRecord.incrementAndGet() % 25000 == 0) {
                LOGGER.debug("processed {} records", nrRecord);
              }
            });
  }

  private List<Decision> processRecord(
      VcfRecord vcfRecord, DecisionTree decisionTree, VcfMetadata vcfMetadata) {
    int nrAltAlleles = vcfRecord.getNrAltAlleles();
    List<Decision> decisions;
    if (nrAltAlleles == 1) {
      Decision decision = processVariant(vcfRecord, 0, decisionTree, vcfMetadata);
      decisions = singletonList(decision);
    } else {
      decisions = new ArrayList<>(nrAltAlleles);
      for (int i = 0; i < nrAltAlleles; ++i) {
        Decision decision = processVariant(vcfRecord, i, decisionTree, vcfMetadata);
        decisions.add(decision);
      }
    }
    return decisions;
  }

  private Decision processVariant(
      VcfRecord vcfRecord, int altAlleleIndex, DecisionTree decisionTree, VcfMetadata vcfMetadata) {
    Allele allele = vcfRecord.getAltAllele(altAlleleIndex);
    Variant variant = new Variant(vcfMetadata, vcfRecord, allele);
    return decisionTreeExecutor.execute(decisionTree, variant);
  }
}
