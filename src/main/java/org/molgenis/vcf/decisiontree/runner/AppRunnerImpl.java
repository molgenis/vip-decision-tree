package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppRunnerImpl implements AppRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppRunnerImpl.class);

  private final Classifier classifier;
  private final VcfReader vcfReader;
  private final DecisionTree decisionTree;
  private final ConsequenceAnnotator consequenceAnnotator;
  private final RecordWriter recordWriter;

  AppRunnerImpl(
      Classifier classifier,
      VcfReader vcfReader,
      DecisionTree decisionTree,
      ConsequenceAnnotator consequenceAnnotator,
      RecordWriter recordWriter) {
    this.classifier = requireNonNull(classifier);
    this.vcfReader = requireNonNull(vcfReader);
    this.decisionTree = requireNonNull(decisionTree);
    this.recordWriter = requireNonNull(recordWriter);
    this.consequenceAnnotator = requireNonNull(consequenceAnnotator);
  }

  public void run() {
    LOGGER.info("classifying variants with decision tree ...");
    classifier.classify(vcfReader, decisionTree, recordWriter, consequenceAnnotator);
    LOGGER.info("done");
  }

  @Override
  public void close() {
    try {
      recordWriter.close();
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
