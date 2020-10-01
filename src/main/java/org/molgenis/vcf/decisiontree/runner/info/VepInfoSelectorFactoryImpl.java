package org.molgenis.vcf.decisiontree.runner.info;

import org.springframework.stereotype.Component;

@Component
public class VepInfoSelectorFactoryImpl implements VepInfoSelectorFactory {

  @Override
  public VepInfoSelector create() {
    return new VepInfoSelector();
  }
}
