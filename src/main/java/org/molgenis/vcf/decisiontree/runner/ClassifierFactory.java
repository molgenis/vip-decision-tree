package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.*;
import org.molgenis.vcf.decisiontree.filter.model.SamplesContext;

interface ClassifierFactory {

  Classifier create(Settings settings, RecordWriter recordWriter,
                    VcfMetadata vcfMetadata, VipScoreAnnotator vipScoreAnnotator);

  Classifier create(Settings settings,
      RecordWriter recordWriter, SampleAnnotator sampleAnnotator, SamplesContext samplesContext);
}
