package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import htsjdk.variant.variantcontext.CommonInfo;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFConstants;
import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

public class DecisionWriterImpl implements DecisionWriter {
  static final String INFO_CLASS_ID = "VIPC";
  static final String INFO_PATH_ID = "VIPP";
  static final String INFO_LABELS_ID = "VIPL";

  private final VariantContextWriter vcfWriter;
  private final boolean writeLabels;
  private final boolean writePaths;

  DecisionWriterImpl(VariantContextWriter vcfWriter, boolean writeLabels, boolean writePaths) {
    this.vcfWriter = requireNonNull(vcfWriter);
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
  }

  @Override
  public void write(List<Decision> decisions, VariantContext vcfRecord) {
    VariantContext updatedVariantContext = addDecisions(decisions, vcfRecord);
    vcfWriter.add(updatedVariantContext);
  }

  private VariantContext addDecisions(List<Decision> decisions, VariantContext vcfRecord) {
    CommonInfo commonInfo = vcfRecord.getCommonInfo();

    addDecisionClass(decisions, commonInfo);
    if (writePaths) {
      addDecisionPath(decisions, commonInfo);
    }

    if (writeLabels) {
      addDecisionLabels(decisions, commonInfo);
    }
    return vcfRecord;
  }

  private static void addDecisionLabels(List<Decision> decisions, CommonInfo commonInfo) {
    String infoLabelsValue;
    if (decisions.size() == 1) {
      infoLabelsValue =
          decisions.get(0).getLabels().stream().map(Label::getId).collect(joining(","));
    } else {
      infoLabelsValue =
          decisions.stream()
              .map(Decision::getLabels)
              .map(
                  labels ->
                      labels.isEmpty()
                          ? VCFConstants.MISSING_VALUE_v4
                          : labels.stream().map(Label::getId).collect(joining("|")))
              .collect(joining(","));
    }
    commonInfo.putAttribute(INFO_LABELS_ID, infoLabelsValue);
  }

  private static void addDecisionPath(List<Decision> decisions, CommonInfo commonInfo) {
    String infoPathValue;
    if (decisions.size() == 1) {
      infoPathValue = decisions.get(0).getPath().stream().map(Node::getId).collect(joining(","));
    } else {
      infoPathValue =
          decisions.stream()
              .map(Decision::getPath)
              .map(nodes -> nodes.stream().map(Node::getId).collect(joining("|")))
              .collect(joining(","));
    }
    commonInfo.putAttribute(INFO_PATH_ID, infoPathValue);
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
