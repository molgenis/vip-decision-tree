package org.molgenis.vcf.decisiontree.filter;


public class VipScoreAnnotatorImpl implements VipScoreAnnotator{

    private final boolean writeLabels;
    private final boolean writePaths;

    public VipScoreAnnotatorImpl(boolean writeLabels, boolean writePaths) {
        this.writeLabels = writeLabels;
        this.writePaths = writePaths;
    }

    @Override
    public String annotate(Integer score, String consequence) {
        StringBuilder csqBuilder = new StringBuilder(consequence);
        csqBuilder.append("|");
        csqBuilder.append(score);
        if (writePaths) {
            csqBuilder.append("|");
            csqBuilder.append(score);
        }
        if (writeLabels) {
            csqBuilder.append("|");
            csqBuilder.append(score);
        }
        return csqBuilder.toString();
    }
}