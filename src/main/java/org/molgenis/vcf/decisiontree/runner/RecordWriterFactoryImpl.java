package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;
import static org.molgenis.vcf.decisiontree.ModifiedVcfWriter.getWriterThread;
import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.VIPC_S;
import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.VIPP_S;
import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.VIPL_S;
import static org.molgenis.vcf.utils.utils.HeaderUtils.fixVcfFilterHeaderLines;
import static org.molgenis.vcf.utils.utils.HeaderUtils.fixVcfFormatHeaderLines;
import static org.molgenis.vcf.utils.utils.HeaderUtils.fixVcfInfoHeaderLines;

import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.molgenis.vcf.decisiontree.AppSettings;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.WriterSettings;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.RecordWriterImpl;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.molgenis.vcf.decisiontree.runner.info.MissingVepException;
import org.springframework.stereotype.Component;

@Component
class RecordWriterFactoryImpl implements RecordWriterFactory {

  private static final String HEADER_VIP_VERSION = "VIP_treeVersion";
  private static final String HEADER_VIP_ARGS = "VIP_treeCommand";
  private static final String INFO_CLASS_DESC = "VIP decision tree classification";
  private static final String INFO_PATH_DESC = "VIP decision tree path (ampersand separated)";
  private static final String INFO_LABELS_DESC = "VIP decision tree labels (ampersand separated)";
  public static final String INFO_CLASS_ID = "VIPC";
  public static final String INFO_PATH_ID = "VIPP";
  public static final String INFO_LABELS_ID = "VIPL";
  public static final String VIPC_S_DESC = "VIP decision tree classification.";
  public static final String VIPC_S_INFO = "VIP decision tree classification (samples).";
  public static final String VIPP_S_DESC = "VIP decision tree path.";
  public static final String VIPL_S_DESC = "VIP decision tree labels.";

  @Override
  public RecordWriter create(VcfMetadata vcfMetadata, Settings settings) {
    WriterSettings writerSettings = settings.getWriterSettings();

    PipedOutputStream pipedOut = new PipedOutputStream();
    Thread writerThread;
    try {
        writerThread = getWriterThread(pipedOut, writerSettings);
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
    VariantContextWriter vcfWriter = createVcfWriter(writerSettings, pipedOut);
    VCFHeader vcfHeader = createHeader(vcfMetadata, settings);
    vcfWriter.writeHeader(vcfHeader);

    return new RecordWriterImpl(vcfWriter, writerThread);
  }

  private static VariantContextWriter createVcfWriter(WriterSettings settings, OutputStream outputStream) {
    Path outputVcfPath = settings.getOutputVcfPath();
    if (settings.isOverwriteOutput()) {
      try {
        Files.deleteIfExists(outputVcfPath);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    } else if (Files.exists(outputVcfPath)) {
      throw new IllegalArgumentException(
          format("cannot create '%s' because it already exists.", outputVcfPath));
    }

    return new VariantContextWriterBuilder()
        .clearOptions()
        .setOutputStream(outputStream)
        .build();
  }

  private static VCFHeader createHeader(VcfMetadata vcfMetadata, Settings settings) {
    AppSettings appSettings = settings.getAppSettings();
    WriterSettings writerSettings = settings.getWriterSettings();

    VCFHeader vcfHeader = new VCFHeader(vcfMetadata.unwrap());
    vcfHeader.addMetaDataLine(new VCFHeaderLine(HEADER_VIP_VERSION, appSettings.getVersion()));
    vcfHeader.addMetaDataLine(
        new VCFHeaderLine(HEADER_VIP_ARGS, String.join(" ", appSettings.getArgs())));

    if (settings.getMode() == Mode.VARIANT) {
      vcfHeader = addVariantHeaders(vcfMetadata, writerSettings, vcfHeader);
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
      VCFHeader vcfHeader) {
    VCFInfoHeaderLine vepHeader = null;

    Collection<VCFInfoHeaderLine> infoHeaderLines = fixVcfInfoHeaderLines(vcfHeader);

    for (VCFInfoHeaderLine infoHeaderLine : infoHeaderLines) {
      if (infoHeaderLine.getID().equals(vcfMetadata.getVepHeaderLine().getParentField().getId())) {
        vepHeader = infoHeaderLine;
        infoHeaderLines.remove(infoHeaderLine);
        break;
      }
    }

    if (vepHeader == null) {
      throw new MissingVepException();
    }

    Set<VCFHeaderLine> additionalInfoLines = new HashSet<>();
    StringBuilder vepDescriptionBuilder = new StringBuilder(vepHeader.getDescription());
    vepDescriptionBuilder.append("|");
    vepDescriptionBuilder.append(INFO_CLASS_ID);
    additionalInfoLines.add(new VCFHeaderLine(INFO_CLASS_ID, INFO_CLASS_DESC));
    if (writerSettings.isWritePath()) {
      vepDescriptionBuilder.append("|");
      vepDescriptionBuilder.append(INFO_PATH_ID);
      additionalInfoLines.add(new VCFHeaderLine(INFO_PATH_ID, INFO_PATH_DESC));
    }
    if (writerSettings.isWriteLabels()) {
      vepDescriptionBuilder.append("|");
      vepDescriptionBuilder.append(INFO_LABELS_ID);
      additionalInfoLines.add(new VCFHeaderLine(INFO_LABELS_ID, INFO_LABELS_DESC));
    }

    Set<VCFHeaderLine> headerLines = new HashSet<>();
    headerLines.add(
        new VCFInfoHeaderLine(vepHeader.getID(), vepHeader.getCountType(), vepHeader.getType(),
            vepDescriptionBuilder.toString(), vepHeader.getSource(), vepHeader.getVersion()));
    headerLines.addAll(fixVcfFormatHeaderLines(vcfHeader));
    headerLines.addAll(fixVcfFilterHeaderLines(vcfHeader));
    headerLines.addAll(vcfHeader.getOtherHeaderLines());
    headerLines.addAll(vcfHeader.getContigLines());
    headerLines.addAll(infoHeaderLines);
    headerLines.addAll(additionalInfoLines);
    vcfHeader = new VCFHeader(headerLines, vcfHeader.getGenotypeSamples());
    return vcfHeader;
  }
}
