package com.speccy.ddd;

public interface DomainRule {
    boolean isBroken();

    String message();
}