package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppRunnerImpl implements AppRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppRunnerImpl.class);

  private final Classifier classifier;
  private final VCFFileReader vcfFileReader;
  private final DecisionTree decisionTree;
  private final DecisionWriter decisionWriter;

  AppRunnerImpl(
      Classifier classifier,
      VCFFileReader vcfFileReader,
      DecisionTree decisionTree,
      DecisionWriter decisionWriter) {
    this.classifier = requireNonNull(classifier);
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.decisionTree = requireNonNull(decisionTree);
    this.decisionWriter = requireNonNull(decisionWriter);
  }

  public void run() {
    LOGGER.info("classifying variants with decision tree ...");
    classifier.classify(vcfFileReader, decisionTree, decisionWriter, vcfFileReader.getFileHeader());
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
      vcfFileReader.close();
    } catch (Exception e) {
      LOGGER.error("error closing reader", e);
    }
  }
}
