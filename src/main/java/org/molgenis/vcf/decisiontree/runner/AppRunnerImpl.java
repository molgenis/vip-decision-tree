package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppRunnerImpl implements AppRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppRunnerImpl.class);

  private final Classifier classifier;
  private final VcfReader vcfReader;
  private final DecisionTree decisionTree;
  private final DecisionWriter decisionWriter;

  AppRunnerImpl(
      Classifier classifier,
      VcfReader vcfReader,
      DecisionTree decisionTree,
      DecisionWriter decisionWriter) {
    this.classifier = requireNonNull(classifier);
    this.vcfReader = requireNonNull(vcfReader);
    this.decisionTree = requireNonNull(decisionTree);
    this.decisionWriter = requireNonNull(decisionWriter);
  }

  public void run() {
    LOGGER.info("classifying variants with decision tree ...");
    classifier.classify(vcfReader, decisionTree, decisionWriter);
    LOGGER.info("done");
  }

  @Override
  public void close() {
    try {
      decisionWriter.close();
    } catch (Exception e) {
      LOGGER.error("error closing writer", e);
    }
    try {
      vcfReader.close();
    } catch (Exception e) {
      LOGGER.error("error closing reader", e);
    }
  }
}
