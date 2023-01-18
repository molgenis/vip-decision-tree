package org.molgenis.vcf.decisiontree.runner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {


    @Test
    void calculateScore_emptyStrings() {
        int score = ScoreCalculator.calculateScore("", "", "", "", "", "");

        assertEquals(-1, score);
    }

    @Test
    void calculateScore_onlyGnomad() {
        int score = ScoreCalculator.calculateScore("", "", "", "", "", "1");

        assertEquals(0, score);
    }

    @Test
    void calculateScore_onlyGoodGnomad() {
        int score = ScoreCalculator.calculateScore("", "", "", "", "", "0.001");

        assertEquals(1, score);
    }

    @Test
    void calculateScore_regionAndGnomad() {
        int score = ScoreCalculator.calculateScore("DNase", "", "", "", "", "0.001");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_regionToolAndGnomad() {
        int score = ScoreCalculator.calculateScore("DNase", "", "60.105", "", "", "0.001");

        assertEquals(3, score);
    }

    @Test
    void calculateScore_regionToolGnomadAndConstraint() {
        int score = ScoreCalculator.calculateScore("DNase", "", "60.105", "", "0.99", "0.001");

        assertEquals(4, score);
    }

    @Test
    void calculateScore_regionToolLowScoreHighGnomad() {
        int score = ScoreCalculator.calculateScore("", "0.105", "", "", "", "0.9");

        assertEquals(0, score);
    }

    @Test
    void calculateScore_regionGnomadAndToolLowScoreAndHigh() {
        int score = ScoreCalculator.calculateScore("DNase", "0.905", "0.8", "", "", "0.001");

        assertEquals(3, score);
    }

    @Test
    void calculateScore_regionGnomadWithMultipleToolScores() {
        int score = ScoreCalculator.calculateScore("DNase", "", "0.105&0.88&1.09", "", "", "0.001");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_regionGnomadWithMultipleToolScoresOneHigh() {
        int score = ScoreCalculator.calculateScore("DNase", "", "0.105&0.88&1.09&90.0", "", "", "0.001");

        assertEquals(3, score);
    }

    @Test
    void calculateScore_GnomadMultipleToolScoresOneHigh() {
        int score = ScoreCalculator.calculateScore("", "", "0.105&0.88&1.09&90.0", "", "", "0.001");

        assertEquals(1, score);
    }

    @Test
    void calculateScore_GnomadHighToolScoreAndConstraint() {
        int score = ScoreCalculator.calculateScore("", "", "", "", "0.9", "0.001");

        assertEquals(1, score);
    }

    @Test
    void calculateScore_fathmmHighScorRegionAndGnomad() {
        int score = ScoreCalculator.calculateScore("TFBS", "0.9", "0", "0", "", "0.001");

        assertEquals(3, score);
    }

    @Test
    void calculateScore_reMMHighScore() {
        int score = ScoreCalculator.calculateScore("TFBS", "0", "0", "0.9", "", "0.001");

        assertEquals(3, score);
    }

    @Test
    void calculateScore_onlyConstraint() {
        int score = ScoreCalculator.calculateScore("", "", "", "", "0.9", "0.001");

        assertEquals(1, score);
    }

    @Test
    void calculateScore_regionGnomadAndConstraint() {
        int score = ScoreCalculator.calculateScore("TFBS", "", "", "", "0.9", "0.001");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_regionLowToolHighGnomad() {
        int score = ScoreCalculator.calculateScore("Dnase", "0.05", "48.1",  "", "", "0.2015");

        assertEquals(0, score);
    }
}