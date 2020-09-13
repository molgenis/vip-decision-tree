package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import htsjdk.variant.variantcontext.CommonInfo;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFConstants;
import java.util.List;
import java.util.Set;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

public class DecisionWriterImpl implements DecisionWriter {

  public static final String INFO_CLASS_ID = "VIPC";
  public static final String INFO_PATH_ID = "VIPP";
  public static final String INFO_LABELS_ID = "VIPL";

  private final VariantContextWriter vcfWriter;
  private final boolean writeLabels;
  private final boolean writePaths;

  /**
   * Constructs a DecisionWriter that doesn't store labels and paths.
   */
  public DecisionWriterImpl(VariantContextWriter vcfWriter) {
    this(vcfWriter, false, false);
  }

  public DecisionWriterImpl(
      VariantContextWriter vcfWriter, boolean writeLabels, boolean writePaths) {
    this.vcfWriter = requireNonNull(vcfWriter);
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
  }

  @Override
  public void write(List<Decision> decisions, VcfRecord vcfRecord) {
    VariantContext variantContext = vcfRecord.unwrap();
    VariantContext updatedVariantContext = addDecisions(decisions, variantContext);
    vcfWriter.add(updatedVariantContext);
  }

  private VariantContext addDecisions(List<Decision> decisions, VariantContext variantContext) {
    CommonInfo commonInfo = variantContext.getCommonInfo();

    addDecisionClass(decisions, commonInfo);
    if (writePaths) {
      addDecisionsPath(decisions, commonInfo);
    }

    if (writeLabels) {
      addDecisionLabels(decisions, commonInfo);
    }
    return variantContext;
  }

  private static void addDecisionLabels(List<Decision> decisions, CommonInfo commonInfo) {
    String infoLabelsValue;
    if (decisions.size() == 1) {
      infoLabelsValue = getVcfLabel(decisions.get(0));
    } else {
      infoLabelsValue =
          decisions.stream().map(DecisionWriterImpl::getVcfLabel).collect(joining(","));
    }
    commonInfo.putAttribute(INFO_LABELS_ID, infoLabelsValue);
  }

  private static String getVcfLabel(Decision decision) {
    String decisionLabel;
    Set<Label> labels = decision.getLabels();
    if (!labels.isEmpty()) {
      decisionLabel = labels.stream().map(Label::getId).collect(joining("|"));
    } else {
      decisionLabel = VCFConstants.MISSING_VALUE_v4;
    }
    return decisionLabel;
  }

  private static void addDecisionsPath(List<Decision> decisions, CommonInfo commonInfo) {
    String infoPathValue;
    if (decisions.size() == 1) {
      infoPathValue = getVcfPath(decisions.get(0));
    } else {
      infoPathValue = decisions.stream().map(DecisionWriterImpl::getVcfPath).collect(joining(","));
    }
    commonInfo.putAttribute(INFO_PATH_ID, infoPathValue);
  }

  private static String getVcfPath(Decision decision) {
    String decisionPath;
    List<Node> path = decision.getPath();
    if (!path.isEmpty()) {
      decisionPath = path.stream().map(Node::getId).collect(joining("|"));
    } else {
      decisionPath = VCFConstants.MISSING_VALUE_v4;
    }
    return decisionPath;
  }

  private static void addDecisionClass(List<Decision> decisions, CommonInfo commonInfo) {
    String infoClassValue;
    if (decisions.size() == 1) {
      infoClassValue = decisions.get(0).getClazz();
    } else {
      infoClassValue = decisions.stream().map(Decision::getClazz).collect(joining(","));
    }
    commonInfo.putAttribute(INFO_CLASS_ID, infoClassValue);
  }

  @Override
  public void close() {
    vcfWriter.close();
  }
}
