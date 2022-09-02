package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionClass;
import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionLabelsString;
import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionsPath;

import org.molgenis.vcf.decisiontree.filter.model.Decision;

public class ConsequenceAnnotatorImpl implements ConsequenceAnnotator {

  private final boolean writeLabels;
  private final boolean writePaths;

  public ConsequenceAnnotatorImpl(boolean writeLabels, boolean writePaths) {
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
  }

  @Override
  public String annotate(Decision decision, String consequence) {
    StringBuilder csqBuilder = new StringBuilder(consequence);
    csqBuilder.append("|");
    csqBuilder.append(getDecisionClass(decision));
    if (writePaths) {
      csqBuilder.append("|");
      csqBuilder.append(getDecisionsPath(decision));
    }
    if (writeLabels) {
      csqBuilder.append("|");
      csqBuilder.append(getDecisionLabelsString(decision));
    }
    return csqBuilder.toString();
  }
}
