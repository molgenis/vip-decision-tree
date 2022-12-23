package org.molgenis.vcf.decisiontree.filter;

public class VipScoreAnnotator {

    private final boolean writeLabels;
    private final boolean writePaths;

    public VipScoreAnnotator(boolean writeLabels, boolean writePaths) {
        this.writeLabels = writeLabels;
        this.writePaths = writePaths;
    }

    public String annotate(int level, String consequence) {
        StringBuilder csqBuilder = new StringBuilder(consequence);
        csqBuilder.append("|");
        csqBuilder.append(level);
        if (writePaths) {
            csqBuilder.append("|");
            csqBuilder.append(level);
        }
        if (writeLabels) {
            csqBuilder.append("|");
            csqBuilder.append(level);
        }
        return csqBuilder.toString();
    }
}