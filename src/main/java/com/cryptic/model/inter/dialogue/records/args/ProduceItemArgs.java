package com.cryptic.model.inter.dialogue.records.args;

public record ProduceItemArgs(String title, int total, int lastAmount, int... items){}
