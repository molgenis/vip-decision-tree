package org.molgenis.vcf.decisiontree.loader.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode.Type;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Node {

  String id;
  String label;
  boolean leaf;
}
