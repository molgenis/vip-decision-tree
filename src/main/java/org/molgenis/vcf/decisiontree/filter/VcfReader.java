package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataParser;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final VepMetadataParser vepMetadataParser;
  private boolean inited = false;
  private NestedHeaderLine vepNestedHeaderLine = null;
  private NestedHeaderLine gtNestedHeaderLine = null;

  public VcfReader(VCFFileReader vcfFileReader, VepMetadataParser vepMetadataParser) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.vepMetadataParser = requireNonNull(vepMetadataParser);
  }

  private void initNestedMeta() {
    if (!inited) {
      vepNestedHeaderLine = vepMetadataParser.map(vcfFileReader.getFileHeader());
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfFileReader.getFileHeader(), vepNestedHeaderLine, gtNestedHeaderLine);
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
