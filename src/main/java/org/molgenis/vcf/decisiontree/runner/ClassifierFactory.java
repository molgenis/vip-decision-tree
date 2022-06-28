package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.sample.SampleAnnotator;

interface ClassifierFactory {

  Classifier create(Settings settings, DecisionTree deisionTree,
      ConsequenceAnnotator consequenceAnnotator, RecordWriter recordWriter,
      VcfMetadata vcfMetadata);

  Classifier create(Settings settings, DecisionTree decisionTree, SampleAnnotator sampleAnnotator,
      RecordWriter recordWriter);
}
