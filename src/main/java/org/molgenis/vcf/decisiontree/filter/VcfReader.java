package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.stream.Stream;

import htsjdk.variant.vcf.VCFIterator;
import org.molgenis.vcf.decisiontree.runner.info.GenotypeMetadataMapper;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataParser;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFIterator vcfIterator;
  private final VepMetadataParser vepMetadataParser;
  private final boolean strict;
  private final GenotypeMetadataMapper genotypeMetadataMapper;
  private boolean inited = false;
  private NestedHeaderLine vepNestedHeaderLine = null;
  private NestedHeaderLine gtNestedHeaderLine = null;

  public VcfReader(VCFIterator vcfIterator, VepMetadataParser vepMetadataParser,
                   GenotypeMetadataMapper genotypeMetadataMapper,
                   boolean strict) {
    this.vcfIterator = requireNonNull(vcfIterator);
    this.vepMetadataParser = requireNonNull(vepMetadataParser);
    this.genotypeMetadataMapper = requireNonNull(genotypeMetadataMapper);
    this.strict = strict;
  }

  private void initNestedMeta() {
    if (!inited) {
      vepNestedHeaderLine = vepMetadataParser.map(vcfIterator.getHeader());
      gtNestedHeaderLine = genotypeMetadataMapper.map();
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return vcfIterator.stream().map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfIterator.getHeader(), vepNestedHeaderLine, gtNestedHeaderLine,
        strict);
  }

  @Override
  public void close() {
    vcfIterator.close();
  }
}
