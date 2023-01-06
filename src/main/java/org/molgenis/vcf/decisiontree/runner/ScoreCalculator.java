package org.molgenis.vcf.decisiontree.runner;

import java.util.*;

public class ScoreCalculator {

    public static int calculateScore(String region, String ncER, String fathmm, String reMM, String constraint) {
        // level 1: overlap with a region
        // level 2: score of ncER(>49.9) fathmm(>0.5) ReMM*(>0.5)
        // level 3: level 2 + constraint region above or equal to 0.7
        int vipVaranScore  = 0;
        if (!region.isEmpty() ) {
            vipVaranScore = 1;
        }
        if (goodToolScore(ncER, fathmm, reMM) || getMaxScore(constraint.split("&")) >= 0.7) {
            vipVaranScore = 2;
        }
        if (goodToolScore(ncER, fathmm, reMM) & getMaxScore(constraint.split("&")) >= 0.7) {
            vipVaranScore = 3;
        }

        return vipVaranScore;
    }

    private static boolean goodToolScore(String ncER, String fathmm, String reMM) {
        double ncERscore = getMaxScore(ncER.split("&"));
        double fathmmScore = getMaxScore(fathmm.split("&"));
        double reMMScore = getMaxScore(reMM.split("&"));

        return ncERscore > 49.9 || fathmmScore > 0.5 || reMMScore > 0.5;
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
