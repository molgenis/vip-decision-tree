package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VipScoreAnnotator;
import org.molgenis.vcf.decisiontree.filter.VipScoreAnnotatorImpl;
import org.springframework.stereotype.Component;

@Component
class VipScoreAnnotatorFactoryImpl implements VipScoreAnnotatorFactory {

    private VipScoreAnnotatorFactoryImpl() {

    }

    @Override
    public VipScoreAnnotator create(Settings settings) {
        return new VipScoreAnnotatorImpl(settings.getWriterSettings().isWriteLabels(),
                settings.getWriterSettings().isWritePath());
    }
}
