package org.molgenis.vcf.decisiontree.visualizer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Node {
  String id;
  String label;
  boolean leaf;
}
