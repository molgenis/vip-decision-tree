package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.WriterSettings;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.RecordWriterImpl;
import org.springframework.stereotype.Component;

@Component
public class RecordWriterFactoryImpl implements RecordWriterFactory {

  @Override
  public RecordWriter create(VCFHeader vcfHeader, Settings settings) {
    WriterSettings writerSettings = settings.getWriterSettings();

    VariantContextWriter vcfWriter = createVcfWriter(writerSettings);
    vcfWriter.writeHeader(vcfHeader);
    return new RecordWriterImpl(vcfWriter);
  }

  private static VariantContextWriter createVcfWriter(WriterSettings settings) {
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
        .setOutputFile(outputVcfPath.toFile())
        .build();
  }
}
