package com.aelous.utility.test.generic;

import com.aelous.utility.Utils;

public class FractionDecimalTest {

    public static void main(String[] args) {
        //This is the same as 1 / 850
        float testDecimal = Utils.getDecimalFromFraction(1, 850);
        System.out.println("The test decimal is: " + testDecimal);

        //This is the same as 1 / 850 * 100
        float testPercent = Utils.getPercentageFromDenominator(850);
        System.out.println("The test percent is: " + testPercent + " with test fraction " + Utils.getFractionFromPercentage(testPercent));

        //This is the same as 1 / 850
        float decimal = Utils.getDecimalFromDenominator(850);
        System.out.println("The decimal is: " + decimal);

        //This is the same as 0.00117647058 * 100
        float percentage = Utils.getPercentageFromDecimal(decimal);
        System.out.println("The percentage is: " + percentage);

        //This is the same as 1 / 0.11764705882 * 100
        float denominator = Utils.getDenominatorFromPercentage(percentage);
        System.out.println("The denominator is: " + denominator);

        //This is the same as 1 / 0.11764705882 * 100
        String fraction = Utils.getFractionFromPercentage(percentage);
        System.out.println("The fraction is: " + fraction);


        float testFloat = 0.02f;
        String fract = Utils.getFractionFromPercentage(testFloat);
        System.out.println("The fraction is: " + fract + " with chance " + testFloat);
    }

}
