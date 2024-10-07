package com.abnamro.packageshippingservice.model.enums;

public enum PackageSizeEnum {

    S(0.2),

    M(1),

    L(10),

    XL(1000); // good to check upper limit and update here.

    private double weightLimit;

    PackageSizeEnum(double weightLimit) {
        this.weightLimit = weightLimit;
    }

    public double getValue() {
        return weightLimit;
    }


    /**
     * Converts the package weight in grams to a size category.
     *
     * @param weightInGrams The weight of the package in grams.
     * @return The corresponding PackageSizeEnum based on the weight thresholds.
     */
    public static PackageSizeEnum fromWeight(double weightInGrams) {
        for (PackageSizeEnum size : values()) {
            if (weightInGrams < size.weightLimit) {
                return size;
            }
        }
        return XL;
    }
}
