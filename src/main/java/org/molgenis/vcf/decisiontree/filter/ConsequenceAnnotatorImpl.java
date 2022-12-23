package org.molgenis.vcf.decisiontree.filter;

public class ConsequenceAnnotatorImpl implements ConsequenceAnnotator {

  private final boolean writeLabels;
  private final boolean writePaths;

  public ConsequenceAnnotatorImpl(boolean writeLabels, boolean writePaths) {
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
  }

  @Override
  public String annotate(String consequence) {
    StringBuilder csqBuilder = new StringBuilder(consequence);
    csqBuilder.append("|");
    if (writePaths) {
      csqBuilder.append("|");
    }
    if (writeLabels) {
      csqBuilder.append("|");
    }
    return csqBuilder.toString();
  }
}
