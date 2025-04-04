package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.AppSettings;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.WriterSettings;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.*;
import static org.molgenis.vcf.decisiontree.runner.HeaderAnnotator.*;

@ExtendWith(MockitoExtension.class)
class HeaderAnnotatorTest {

    @Mock
    private VcfMetadata vcfMetadata;
    @Mock
    Settings settings;
    @Mock
    WriterSettings writerSettings;
    @Mock
    AppSettings appSettings;
    @Mock
    VCFInfoHeaderLine vepInfoHeaderLine;
    @Mock
    NestedHeaderLine nestedVepHeaderLine;
    @Mock
    private Field vepField;
    VCFHeader vcfHeader;

    @BeforeEach
    void setUp() {
        when(vepInfoHeaderLine.getID()).thenReturn("CSQ");
        when(vepInfoHeaderLine.getKey()).thenReturn("CSQ");
        vcfHeader = new VCFHeader();
        vcfHeader.setVCFHeaderVersion(VCFHeaderVersion.VCF4_2);
        vcfHeader.addMetaDataLine(vepInfoHeaderLine);
        when(vcfMetadata.unwrap()).thenReturn(vcfHeader);
        when(settings.getWriterSettings()).thenReturn(writerSettings);
        when(settings.getAppSettings()).thenReturn(appSettings);
    }

    @Test
    void annotateHeaderVariant() {
        when(vcfMetadata.getVepHeaderLine()).thenReturn(nestedVepHeaderLine);
        when(vepField.getId()).thenReturn("CSQ");
        when(vepInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.String);
        when(vepInfoHeaderLine.getDescription()).thenReturn("Consequence annotations from Ensembl VEP. Format: Allele|test1|test2");
        when(nestedVepHeaderLine.getParentField()).thenReturn(vepField);
        when(settings.getWriterSettings()).thenReturn(writerSettings);
        when(settings.getAppSettings()).thenReturn(appSettings);
        when(settings.getMode()).thenReturn(Mode.VARIANT);
        when(writerSettings.isWriteLabels()).thenReturn(true);
        when(writerSettings.isWritePath()).thenReturn(true);
        VCFHeader actual = HeaderAnnotator.annotateHeader(vcfMetadata, settings);
        assertTrue(actual.getOtherHeaderLines().containsAll(Set.of(new VCFHeaderLine(INFO_CLASS_ID, INFO_CLASS_DESC), new VCFHeaderLine(INFO_PATH_ID, INFO_PATH_DESC), new VCFHeaderLine(INFO_LABELS_ID, INFO_LABELS_DESC))));
    }

    @Test
    void annotateHeaderVariantNoLabel() {
        when(vcfMetadata.getVepHeaderLine()).thenReturn(nestedVepHeaderLine);
        when(vepField.getId()).thenReturn("CSQ");
        when(vepInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.String);
        when(vepInfoHeaderLine.getDescription()).thenReturn("Consequence annotations from Ensembl VEP. Format: Allele|test1|test2");
        when(nestedVepHeaderLine.getParentField()).thenReturn(vepField);
        when(settings.getWriterSettings()).thenReturn(writerSettings);
        when(settings.getAppSettings()).thenReturn(appSettings);
        when(settings.getMode()).thenReturn(Mode.VARIANT);
        when(writerSettings.isWriteLabels()).thenReturn(false);
        when(writerSettings.isWritePath()).thenReturn(true);
        VCFHeader actual = HeaderAnnotator.annotateHeader(vcfMetadata, settings);
        assertTrue(actual.getOtherHeaderLines().containsAll(Set.of(new VCFHeaderLine(INFO_CLASS_ID, INFO_CLASS_DESC), new VCFHeaderLine(INFO_PATH_ID, INFO_PATH_DESC))));
        assertFalse(actual.getOtherHeaderLines().contains(new VCFHeaderLine(INFO_LABELS_ID, INFO_LABELS_DESC)));
    }

    @Test
    void annotateHeaderVariantNoLabelNoPath() {
        when(vcfMetadata.getVepHeaderLine()).thenReturn(nestedVepHeaderLine);
        when(vepField.getId()).thenReturn("CSQ");
        when(vepInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.String);
        when(vepInfoHeaderLine.getDescription()).thenReturn("Consequence annotations from Ensembl VEP. Format: Allele|test1|test2");
        when(nestedVepHeaderLine.getParentField()).thenReturn(vepField);
        when(settings.getMode()).thenReturn(Mode.VARIANT);
        when(writerSettings.isWriteLabels()).thenReturn(false);
        when(writerSettings.isWritePath()).thenReturn(false);
        VCFHeader actual = HeaderAnnotator.annotateHeader(vcfMetadata, settings);
        assertTrue(actual.getOtherHeaderLines().contains(new VCFHeaderLine(INFO_CLASS_ID, INFO_CLASS_DESC)));
        assertFalse(actual.getOtherHeaderLines().contains(new VCFHeaderLine(INFO_LABELS_ID, INFO_LABELS_DESC)));
        assertFalse(actual.getOtherHeaderLines().contains(new VCFHeaderLine(INFO_PATH_ID, INFO_PATH_DESC)));
    }

    @Test
    void annotateHeaderSample() {
        when(settings.getMode()).thenReturn(Mode.SAMPLE);
        when(writerSettings.isWriteLabels()).thenReturn(true);
        when(writerSettings.isWritePath()).thenReturn(true);
        VCFHeader actual = HeaderAnnotator.annotateHeader(vcfMetadata, settings);
        assertTrue(actual.getFormatHeaderLines().containsAll(Set.of(new VCFFormatHeaderLine(VIPC_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPC_S_DESC), new VCFFormatHeaderLine(VIPP_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPP_S_DESC), new VCFFormatHeaderLine(VIPL_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPL_S_DESC))));
    }

    @Test
    void annotateHeaderSampleNoLabel() {
        when(settings.getMode()).thenReturn(Mode.SAMPLE);
        when(writerSettings.isWriteLabels()).thenReturn(false);
        when(writerSettings.isWritePath()).thenReturn(true);
        VCFHeader actual = HeaderAnnotator.annotateHeader(vcfMetadata, settings);
        assertTrue(actual.getFormatHeaderLines().containsAll(Set.of(new VCFFormatHeaderLine(VIPC_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPC_S_DESC), new VCFFormatHeaderLine(VIPP_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPP_S_DESC))));
        assertFalse(actual.getFormatHeaderLines().contains(new VCFFormatHeaderLine(VIPL_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPL_S_DESC)));
    }

    @Test
    void annotateHeaderSampleNoLabelNoPath() {
        when(settings.getMode()).thenReturn(Mode.SAMPLE);
        when(writerSettings.isWriteLabels()).thenReturn(false);
        when(writerSettings.isWritePath()).thenReturn(false);
        VCFHeader actual = HeaderAnnotator.annotateHeader(vcfMetadata, settings);
        assertTrue(actual.getFormatHeaderLines().contains(new VCFFormatHeaderLine(VIPC_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPC_S_DESC)));
        assertFalse(actual.getFormatHeaderLines().contains(new VCFFormatHeaderLine(VIPP_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPP_S_DESC)));
        assertFalse(actual.getFormatHeaderLines().contains(new VCFFormatHeaderLine(VIPL_S, VCFHeaderLineCount.UNBOUNDED,VCFHeaderLineType.String,VIPL_S_DESC)));
    }
}