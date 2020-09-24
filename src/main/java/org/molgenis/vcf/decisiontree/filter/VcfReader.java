package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.runner.info.NestedMetadataService;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final NestedMetadataService nestedMetadataService;
  private Map<String, Map<String, NestedField>> nestedMetadata;
  private boolean inited = false;

  public VcfReader(VCFFileReader vcfFileReader,
      NestedMetadataService nestedMetadataService) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.nestedMetadataService = requireNonNull(nestedMetadataService);
  }

  private void initNestedMeta(){
    if(!inited){
      nestedMetadata = nestedMetadataService.map(vcfFileReader.getFileHeader());
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfFileReader.getFileHeader(), nestedMetadata);
  }

  public VCFFileReader unwrap() {
    return vcfFileReader;
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
