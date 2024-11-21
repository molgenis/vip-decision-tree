package org.molgenis.vcf.decisiontree.filter.model;

public interface Node {
  NodeType getNodeType();

  String getId();

  String getDescription();

  String getLabel();
}
