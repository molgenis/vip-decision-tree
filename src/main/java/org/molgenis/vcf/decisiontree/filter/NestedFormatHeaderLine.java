package org.molgenis.vcf.decisiontree.filter;

import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.Field;

import java.util.Map;

@Value
public class NestedFormatHeaderLine {
    Map<String, Field> formatFields;
}
