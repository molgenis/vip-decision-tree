package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.molgenis.vcf.decisiontree.runner.info.VepHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataParser;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final VepMetadataParser vepMetadataParser;
  private final boolean strict;
  private boolean inited = false;
  private VepHeaderLine vepHeaderLine = null;

  public VcfReader(VCFFileReader vcfFileReader, VepMetadataParser vepMetadataParser,
      boolean strict) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.vepMetadataParser = requireNonNull(vepMetadataParser);
    this.strict = strict;
  }

  private void initNestedMeta() {
    if (!inited) {
      vepHeaderLine = vepMetadataParser.map(vcfFileReader.getFileHeader());
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfFileReader.getFileHeader(), vepHeaderLine, strict);
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
