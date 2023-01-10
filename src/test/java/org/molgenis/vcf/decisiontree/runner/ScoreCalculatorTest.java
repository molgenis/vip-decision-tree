package org.molgenis.vcf.decisiontree.runner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {

    @Test
    void calculateScore_emptyStrings() {
        int score = ScoreCalculator.calculateScore("", "", "", "", "");

        assertEquals(0, score);
    }

    @Test
    void calculateScore_onlyRegion() {
        int score = ScoreCalculator.calculateScore("DNase", "", "", "", "");

        assertEquals(1, score);
    }

    @Test
    void calculateScore_regionAndOneTool() {
        int score = ScoreCalculator.calculateScore("DNase", "60.105", "", "", "");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_regionToolAndConstraint() {
        int score = ScoreCalculator.calculateScore("DNase", "60.105", "", "", "0.99");

        assertEquals(3, score);
    }

    @Test
    void calculateScore_regionToolLowScore() {
        int score = ScoreCalculator.calculateScore("DNase", "0.105", "", "", "");

        assertEquals(1, score);
    }

    @Test
    void calculateScore_regionToolLowScoreAndHigh() {
        int score = ScoreCalculator.calculateScore("DNase", "0.105", "0.8", "", "");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_regionWithMultipleToolScores() {
        int score = ScoreCalculator.calculateScore("DNase", "0.105&0.88&1.09", "", "", "");

        assertEquals(1, score);
    }

    @Test
    void calculateScore_regionWithMultipleToolScoresOneHigh() {
        int score = ScoreCalculator.calculateScore("DNase", "0.105&0.88&1.09&90.0", "", "", "");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_MultipleToolScoresOneHigh() {
        int score = ScoreCalculator.calculateScore("", "0.105&0.88&1.09&90.0", "", "", "");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_HighToolScoreAndConstraint() {
        int score = ScoreCalculator.calculateScore("", "90.0", "", "", "0.9");

        assertEquals(3, score);
    }

    @Test
    void calculateScore_fathmmHighScore() {
        int score = ScoreCalculator.calculateScore("", "0", "0.9", "0", "");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_reMMHighScore() {
        int score = ScoreCalculator.calculateScore("", "0", "0", "0.9", "");

        assertEquals(2, score);
    }

    @Test
    void calculateScore_onlyConstraint() {
        int score = ScoreCalculator.calculateScore("", "", "", "", "0.9");

        assertEquals(1, score);
    }
}