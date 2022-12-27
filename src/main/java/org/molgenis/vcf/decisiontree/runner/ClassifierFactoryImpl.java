package org.molgenis.vcf.decisiontree.runner;


import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.*;
import org.molgenis.vcf.decisiontree.filter.model.SamplesContext;
import org.springframework.stereotype.Component;

@Component
class ClassifierFactoryImpl implements ClassifierFactory {


  ClassifierFactoryImpl() {
  }

  @Override
  public Classifier create(Settings settings, RecordWriter recordWriter,
                           VcfMetadata vcfMetadata, VipScoreAnnotator vipScoreAnnotator) {

    return new AnnotateScoreImpl(new VepHelper(), recordWriter, vcfMetadata, vipScoreAnnotator);
  }

  @Override
  public Classifier create(Settings settings,
                              RecordWriter recordWriter, SampleAnnotator sampleAnnotator, SamplesContext samplesContext) {
    return new SampleClassifierImpl(new VepHelper(),
        recordWriter, sampleAnnotator, samplesContext);
  }
}
