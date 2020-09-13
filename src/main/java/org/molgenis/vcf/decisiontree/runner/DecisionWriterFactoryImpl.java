package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.io.File;
import org.molgenis.vcf.decisiontree.AppSettings;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.WriterSettings;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.DecisionWriterImpl;
import org.springframework.stereotype.Component;

@Component
class DecisionWriterFactoryImpl implements DecisionWriterFactory {

  private static final String HEADER_VIP_VERSION = "VIP_treeVersion";
  private static final String HEADER_VIP_ARGS = "VIP_treeCommand";
  private static final String INFO_CLASS_DESC = "VIP decision tree classification";
  private static final String INFO_PATH_DESC = "VIP decision tree path (pipe separated)";
  private static final String INFO_LABELS_DESC = "VIP decision tree labels (pipe separated)";

  @Override
  public DecisionWriter create(VCFFileReader reader, Settings settings) {
    WriterSettings writerSettings = settings.getWriterSettings();

    VariantContextWriter vcfWriter = createVcfWriter(writerSettings);
    VCFHeader vcfHeader = createHeader(reader, settings);
    vcfWriter.writeHeader(vcfHeader);
    return new DecisionWriterImpl(
        vcfWriter, settings.getWriterSettings().isWriteLabels(), writerSettings.isWritePath());
  }

  // TODO check whether writing .vcf or .vcf.gz based on file extension works
  private static VariantContextWriter createVcfWriter(WriterSettings settings) {
    File outputFile = settings.getOutputVcfPath().toFile();
    return new VariantContextWriterBuilder().clearOptions().setOutputFile(outputFile).build();
  }

  private static VCFHeader createHeader(VCFFileReader reader, Settings settings) {
    AppSettings appSettings = settings.getAppSettings();
    WriterSettings writerSettings = settings.getWriterSettings();

    VCFHeader vcfHeader = new VCFHeader(reader.getFileHeader());
    vcfHeader.addMetaDataLine(new VCFHeaderLine(HEADER_VIP_VERSION, appSettings.getVersion()));
    vcfHeader.addMetaDataLine(
        new VCFHeaderLine(HEADER_VIP_ARGS, String.join(" ", appSettings.getArgs())));

    vcfHeader.addMetaDataLine(
        new VCFInfoHeaderLine(
            DecisionWriterImpl.INFO_CLASS_ID,
            VCFHeaderLineCount.A,
            VCFHeaderLineType.String,
            INFO_CLASS_DESC));

    if (writerSettings.isWritePath()) {
      vcfHeader.addMetaDataLine(
          new VCFInfoHeaderLine(
              DecisionWriterImpl.INFO_PATH_ID,
              VCFHeaderLineCount.A,
              VCFHeaderLineType.String,
              INFO_PATH_DESC));
    }
    if (writerSettings.isWriteLabels()) {
      vcfHeader.addMetaDataLine(
          new VCFInfoHeaderLine(
              DecisionWriterImpl.INFO_LABELS_ID,
              VCFHeaderLineCount.A,
              VCFHeaderLineType.String,
              INFO_LABELS_DESC));
    }
    return vcfHeader;
  }
}
