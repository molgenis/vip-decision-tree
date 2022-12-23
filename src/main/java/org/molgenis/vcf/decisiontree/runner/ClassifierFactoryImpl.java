package org.molgenis.vcf.decisiontree.runner;


import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ClassifierImpl;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.SampleAnnotator;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.SampleClassifierImpl;
import org.molgenis.vcf.decisiontree.filter.model.SamplesContext;
import org.springframework.stereotype.Component;

@Component
class ClassifierFactoryImpl implements ClassifierFactory {


  ClassifierFactoryImpl() {
  }

  @Override
  public Classifier create(Settings settings,
      ConsequenceAnnotator consequenceAnnotator, RecordWriter recordWriter,
      VcfMetadata vcfMetadata) {

    return new ClassifierImpl(new VepHelper(), recordWriter, vcfMetadata); // add VIPScore annotator here
  }

  @Override
  public Classifier create(Settings settings,
      RecordWriter recordWriter, SampleAnnotator sampleAnnotator, SamplesContext samplesContext) {
    return new SampleClassifierImpl(new VepHelper(),
        recordWriter, sampleAnnotator, samplesContext);
  }
}
