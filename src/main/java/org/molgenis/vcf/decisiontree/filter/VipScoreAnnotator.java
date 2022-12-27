package org.molgenis.vcf.decisiontree.filter;

public interface VipScoreAnnotator {

    String annotate(Integer score, String csqString);
}
