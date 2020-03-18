package org.cloud.jpa.domain.enums;

public enum HttpMethod {
    GET(1), POST(2), PUT(4), DELETE(8);

    private int _value;

    HttpMethod(int Value) {
        this._value = Value;
    }

    public int getValue() {
        return _value;
    }

    public static HttpMethod fromInt(int i) {
        for (HttpMethod b : HttpMethod.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }
}

