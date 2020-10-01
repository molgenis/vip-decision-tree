package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.runner.info.VcfNestedMetadataParser;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final VcfNestedMetadataParser vcfNestedMetadataParser;
  private Map<String, Field> nestedFields;
  private boolean inited = false;

  public VcfReader(VCFFileReader vcfFileReader, VcfNestedMetadataParser vcfNestedMetadataParser) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.vcfNestedMetadataParser = requireNonNull(vcfNestedMetadataParser);
  }

  private void initNestedMeta() {
    if (!inited) {
      nestedFields = vcfNestedMetadataParser.map(vcfFileReader.getFileHeader());
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfFileReader.getFileHeader(), nestedFields);
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
