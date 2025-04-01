package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.*;
import org.molgenis.vcf.decisiontree.AppSettings;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.WriterSettings;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.molgenis.vcf.decisiontree.runner.info.MissingVepException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.*;
import static org.molgenis.vcf.utils.utils.HeaderUtils.*;

public class HeaderAnnotator {
    private static final String HEADER_VIP_VERSION = "VIP_treeVersion";
    private static final String HEADER_VIP_ARGS = "VIP_treeCommand";
    static final String INFO_CLASS_DESC = "VIP decision tree classification";
    public static final String INFO_PATH_DESC = "VIP decision tree path (ampersand separated)";
    public static final String INFO_LABELS_DESC = "VIP decision tree labels (ampersand separated)";
    public static final String INFO_CLASS_ID = "VIPC";
    public static final String INFO_PATH_ID = "VIPP";
    public static final String INFO_LABELS_ID = "VIPL";
    public static final String VIPC_S_DESC = "VIP decision tree classification.";
    public static final String VIPC_S_INFO = "VIP decision tree classification (samples).";
    public static final String VIPP_S_DESC = "VIP decision tree path.";
    public static final String VIPL_S_DESC = "VIP decision tree labels.";

    private HeaderAnnotator(){}

    static VCFHeader annotateHeader(VcfMetadata vcfMetadata, Settings settings) {
        AppSettings appSettings = settings.getAppSettings();
        WriterSettings writerSettings = settings.getWriterSettings();

        VCFHeader vcfHeader = new VCFHeader(vcfMetadata.unwrap());
        vcfHeader.addMetaDataLine(new VCFHeaderLine(HEADER_VIP_VERSION, appSettings.getVersion()));
        vcfHeader.addMetaDataLine(
                new VCFHeaderLine(HEADER_VIP_ARGS, String.join(" ", appSettings.getArgs())));

        if (settings.getMode() == Mode.VARIANT) {
            VCFHeaderLine vepHeaderLine = createNewVepHeaderLine(writerSettings, vcfMetadata);
            vcfHeader = addVariantHeaders(vcfMetadata, writerSettings, vcfHeader, vepHeaderLine);
        } else {
            vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(
                    VIPC_S,
                    VCFHeaderLineCount.UNBOUNDED,
                    VCFHeaderLineType.String,
                    VIPC_S_INFO));
            vcfHeader.addMetaDataLine(new VCFFormatHeaderLine(VIPC_S, VCFHeaderLineCount.UNBOUNDED,
                    VCFHeaderLineType.String,
                    VIPC_S_DESC));
            if (writerSettings.isWritePath()) {
                vcfHeader.addMetaDataLine(new VCFFormatHeaderLine(VIPP_S, VCFHeaderLineCount.UNBOUNDED,
                        VCFHeaderLineType.String,
                        VIPP_S_DESC));
            }
            if (writerSettings.isWriteLabels()) {
                vcfHeader.addMetaDataLine(new VCFFormatHeaderLine(VIPL_S, VCFHeaderLineCount.UNBOUNDED,
                        VCFHeaderLineType.String,
                        VIPL_S_DESC));
            }
        }

        return vcfHeader;
    }

    private static VCFHeader addVariantHeaders(VcfMetadata vcfMetadata, WriterSettings writerSettings,
                                               VCFHeader vcfHeader, VCFHeaderLine vepHeaderLine) {
        Collection<VCFInfoHeaderLine> infoHeaderLines = fixVcfInfoHeaderLines(vcfHeader);

        for (VCFInfoHeaderLine infoHeaderLine : infoHeaderLines) {
            if (infoHeaderLine.getID().equals(vcfMetadata.getVepHeaderLine().getParentField().getId())) {
                infoHeaderLines.remove(infoHeaderLine);
                break;
            }
        }

        Set<VCFHeaderLine> additionalInfoLines = new HashSet<>();
        additionalInfoLines.add(new VCFHeaderLine(INFO_CLASS_ID, INFO_CLASS_DESC));
        if (writerSettings.isWritePath()) {
            additionalInfoLines.add(new VCFHeaderLine(INFO_PATH_ID, INFO_PATH_DESC));
        }
        if (writerSettings.isWriteLabels()) {
            additionalInfoLines.add(new VCFHeaderLine(INFO_LABELS_ID, INFO_LABELS_DESC));
        }

        Set<VCFHeaderLine> headerLines = new HashSet<>();
        headerLines.add(vepHeaderLine);
        headerLines.addAll(fixVcfFormatHeaderLines(vcfHeader));
        headerLines.addAll(fixVcfFilterHeaderLines(vcfHeader));
        headerLines.addAll(vcfHeader.getOtherHeaderLines());
        headerLines.addAll(vcfHeader.getContigLines());
        headerLines.addAll(infoHeaderLines);
        headerLines.addAll(additionalInfoLines);
        vcfHeader = new VCFHeader(headerLines, vcfHeader.getGenotypeSamples());
        return vcfHeader;
    }

    private static VCFHeaderLine createNewVepHeaderLine(WriterSettings writerSettings, VcfMetadata vcfMetadata) {
        VCFInfoHeaderLine vepHeader = null;
        for (VCFInfoHeaderLine infoHeaderLine : vcfMetadata.unwrap().getInfoHeaderLines()) {
            if (infoHeaderLine.getID().equals(vcfMetadata.getVepHeaderLine().getParentField().getId())) {
                vepHeader = infoHeaderLine;
                break;
            }
        }

        if (vepHeader == null) {
            throw new MissingVepException();
        }

        StringBuilder vepDescriptionBuilder = new StringBuilder(vepHeader.getDescription());
        vepDescriptionBuilder.append("|");
        vepDescriptionBuilder.append(INFO_CLASS_ID);
        if (writerSettings.isWritePath()) {
            vepDescriptionBuilder.append("|");
            vepDescriptionBuilder.append(INFO_PATH_ID);
        }
        if (writerSettings.isWriteLabels()) {
            vepDescriptionBuilder.append("|");
            vepDescriptionBuilder.append(INFO_LABELS_ID);
        }
        return new VCFInfoHeaderLine(vepHeader.getID(), vepHeader.getCountType(), vepHeader.getType(),
                vepDescriptionBuilder.toString(), vepHeader.getSource(), vepHeader.getVersion());
    }
}
