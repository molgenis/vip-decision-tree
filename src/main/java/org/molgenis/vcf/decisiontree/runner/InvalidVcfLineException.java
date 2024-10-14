package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

import java.io.Serial;

import static java.lang.String.format;

public class InvalidVcfLineException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public InvalidVcfLineException(String line) {
        super(
                format(
                        "VCF line with to little columns detected: %s", line));
    }
}