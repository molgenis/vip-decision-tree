package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionClass;
import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionLabelsString;
import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionsPath;
import static org.molgenis.vcf.decisiontree.runner.HeaderAnnotator.*;
import static org.molgenis.vcf.decisiontree.runner.VepHelper.getVepId;

import htsjdk.variant.vcf.VCFHeader;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.runner.info.MissingVepException;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataMapper;

import java.util.Arrays;

public class ConsequenceAnnotatorImpl implements ConsequenceAnnotator {

  private final boolean writeLabels;
  private final boolean writePaths;
  private final VCFHeader annotatedHeader;
  private final VepMetadataMapper vepMetadataMapper;

  public ConsequenceAnnotatorImpl(boolean writeLabels, boolean writePaths, VCFHeader annotatedHeader, VepMetadataMapper vepMetadataMapper) {
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
    this.annotatedHeader = requireNonNull(annotatedHeader);
    this.vepMetadataMapper = requireNonNull(vepMetadataMapper);
  }

  @Override
  public String annotate(Decision decision, String consequence) {
    String vepId = getVepId(annotatedHeader);
    if(vepId == null){
      throw new MissingVepException();
    }
    NestedHeaderLine vepHeaderLine = vepMetadataMapper.map(vepId, annotatedHeader);
    String[] consequenceArray = consequence.split("\\|", -1);

    Field classField = vepHeaderLine.getField(INFO_CLASS_ID);
    consequenceArray = annotateField(getDecisionClass(decision), classField, consequenceArray);
    if (writePaths) {
      Field pathField = vepHeaderLine.getField(INFO_PATH_ID);
      consequenceArray = annotateField(getDecisionsPath(decision), pathField, consequenceArray);
    }
    if (writeLabels) {
      Field labelsField = vepHeaderLine.getField(INFO_LABELS_ID);
      consequenceArray = annotateField(getDecisionLabelsString(decision), labelsField, consequenceArray);
    }
    return String.join("|", consequenceArray);
  }

  private static String[] annotateField(String annotation, Field field, String[] consequenceArray) {
    if(field instanceof NestedField nestedField) {
      int index = (nestedField).getIndex();
      if (index >= consequenceArray.length) {
        consequenceArray = Arrays.copyOf(consequenceArray, index + 1);
        //Fill newly added items with empty string by default to prevent |null| values in the VEP string
        Arrays.fill(consequenceArray, index + 1, consequenceArray.length, "");
      }
      consequenceArray[index] = annotation;
    } else{
      throw new UnknownFieldException(field.getId(), FieldType.INFO_VEP);
    }
    return consequenceArray;
  }
}
