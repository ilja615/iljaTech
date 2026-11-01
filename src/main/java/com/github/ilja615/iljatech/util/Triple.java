package com.github.ilja615.iljatech.util;

public class Triple<A, B, C> {
    private A a;
    private B b;
    private C c;

    public Triple(A object, B object2, C object3) {
        this.a = object;
        this.b = object2;
        this.c = object3;
    }

    public A getA() {
        return this.a;
    }

    public void setA(A object) {
        this.a = object;
    }

    public B getB() {
        return this.b;
    }

    public void setB(B object2) {
        this.b = object2;
    }

    public C getC() {
        return this.c;
    }

    public void setC(C object3) {
        this.c = object3;
    }
}