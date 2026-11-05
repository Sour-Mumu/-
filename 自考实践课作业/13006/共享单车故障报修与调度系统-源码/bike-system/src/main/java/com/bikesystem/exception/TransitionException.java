package com.bikesystem.exception;

public class TransitionException extends RuntimeException {
    public TransitionException(String msg) { super(msg); }

    public TransitionException(BikeStatus from, BikeStatus to) {
        super(String.format("非法状态转移：%s -> %s", from, to));
    }
}
