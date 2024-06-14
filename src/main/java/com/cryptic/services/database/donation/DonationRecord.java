package com.cryptic.services.database.donation;

public record DonationRecord(int itemId, int itemAmount, int amountPurchased, double productPrice, String message) {
}
