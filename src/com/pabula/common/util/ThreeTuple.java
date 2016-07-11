package com.pabula.common.util;

/**
 * Created by sunsai on 2015/10/21.
 */
public class ThreeTuple<A,B,C> extends TwoTuple<A, B> {
    public final C _3;
    public ThreeTuple(A a, B b, C c) {
        super(a,b);
        _3 = c;
    }

}
