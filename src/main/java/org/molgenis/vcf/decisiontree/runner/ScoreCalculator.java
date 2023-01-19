package org.molgenis.vcf.decisiontree.runner;

import java.util.*;

public class ScoreCalculator {

    public static int calculateScore(String region, String fathmm, String ncER, String reMM, String constraint, String gnomad) {
        // level -2: nothing
        // level -1: if everything is empty
        // level 0: GNOMAD above 1%, scores below FDR50, no region, constraint below 0.7
        // level 1: GNOMAD Rare variant population AF < 1% (0.01)
        // level 2: level 1 + overlap with a region
        // level 3: level 2 + score of ncER(>49.9) fathmm(>0.5) ReMM*(>0.5)
        // level 4: level 3 + constraint region above or equal to 0.7
        int vipVaranScore = -2;

        if (!goodGnomad(gnomad)) {
            vipVaranScore = 0;
        }
        if (goodGnomad(gnomad) ) {
            vipVaranScore = 1;
        }
        if (goodGnomad(gnomad) && !region.isEmpty()) {
            vipVaranScore = 2;
        }
        if (goodGnomad(gnomad) && !region.isEmpty() && goodToolScore(fathmm, ncER, reMM)) {
            vipVaranScore = 3;
        }
        if (goodGnomad(gnomad) && !region.isEmpty() && goodToolScore(fathmm, ncER, reMM) && getMaxScore(constraint.split("&")) >= 0.7) {
            vipVaranScore = 4;
        }
        if (gnomad.isEmpty() && region.isEmpty() && fathmm.isEmpty() && ncER.isEmpty() && reMM.isEmpty() && constraint.isEmpty()) {
            vipVaranScore  = -1;
        }

        return vipVaranScore;
    }

    private static boolean goodToolScore(String fathmm, String ncER, String reMM) {
        double ncERscore = getMaxScore(ncER.split("&"));
        double fathmmScore = getMaxScore(fathmm.split("&"));
        double reMMScore = getMaxScore(reMM.split("&"));

        return ncERscore > 49.9 || fathmmScore > 0.5 || reMMScore > 0.5;
    }

    private static boolean goodGnomad(String gnomad) {
        if (Objects.equals(gnomad, "") || gnomad == null) {
            return true;
        } else {
            double gnomadScore = Double.parseDouble(gnomad);
            return gnomadScore < 0.01;
        }
    }

    private static double getMaxScore(String[] scoreArray) {
        // get max score because if one variant in the region is important the whole region is important
        if (scoreArray.length == 0 || Objects.equals(scoreArray[0], "")) {
            return 0;
        }
        Set<Double> scores = new HashSet<>(scoreArray.length);

        for(String score : scoreArray) {
            scores.add(Double.parseDouble(score));
        }
        return Collections.max(scores);
    }
}
