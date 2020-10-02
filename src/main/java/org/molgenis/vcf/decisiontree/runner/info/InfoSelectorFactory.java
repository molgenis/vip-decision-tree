package org.molgenis.vcf.decisiontree.runner.info;

interface InfoSelectorFactory<E extends NestedInfoSelector> {

  E create();
}
