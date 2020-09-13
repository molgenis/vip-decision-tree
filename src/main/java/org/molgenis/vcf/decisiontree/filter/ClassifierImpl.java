package org.molgenis.vcf.decisiontree.filter;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import java.util.ArrayList;
import java.util.List;
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
  public void classify(
      Iterable<VariantContext> records,
      DecisionTree decisionTree,
      DecisionWriter writer,
      VCFHeader header) {
    int nrRecord = 0;
    for (VariantContext vcfRecord : records) {
      ++nrRecord;

      List<Decision> decisions = processRecord(vcfRecord, decisionTree, header);
      writer.write(decisions, vcfRecord);

      if (nrRecord % 25000 == 0) {
        LOGGER.debug("processed {} records", nrRecord);
      }
    }
  }

  private List<Decision> processRecord(
      VariantContext vcfRecord, DecisionTree decisionTree, VCFHeader header) {
    int nrAltAlleles = vcfRecord.getNAlleles() - 1;
    List<Decision> decisions;
    if (nrAltAlleles == 1) {
      Decision decision = processVariant(vcfRecord, 1, decisionTree, header);
      decisions = singletonList(decision);
    } else {
      decisions = new ArrayList<>(nrAltAlleles);
      for (int i = 0; i < nrAltAlleles; ++i) {
        Decision decision = processVariant(vcfRecord, i + 1, decisionTree, header);
        decisions.add(decision);
      }
    }
    return decisions;
  }

  private Decision processVariant(
      VariantContext vcfRecord, int alleleIndex, DecisionTree decisionTree, VCFHeader header) {
    Variant variant = new Variant(header, vcfRecord, alleleIndex);
    return decisionTreeExecutor.execute(decisionTree, variant);
  }
}
