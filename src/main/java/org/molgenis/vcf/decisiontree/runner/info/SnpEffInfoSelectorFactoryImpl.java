package org.molgenis.vcf.decisiontree.runner.info;

import org.springframework.stereotype.Component;

@Component
public class SnpEffInfoSelectorFactoryImpl implements SnpEffInfoSelectorFactory {

  @Override
  public SnpEffInfoSelector create() {
    return new SnpEffInfoSelector();
  }
}
