package com.hp.c4.rsku.rSku.c4.util;

public final class EperiodType {
	private int _value;
	public static final int _DAILY = 0;
	public static final int _WEEKLY = 1;
	public static final int _MONTHLY = 2;
	public static final int _QUARTERLY = 3;
	public static final EperiodType DAILY = new EperiodType(0);
	public static final EperiodType WEEKLY = new EperiodType(1);
	public static final EperiodType MONTHLY = new EperiodType(2);
	public static final EperiodType QUARTERLY = new EperiodType(3);

	protected EperiodType(int _vis_value) {
		_value = _vis_value;
	}

	public int value() {
		return _value;
	}

	public static EperiodType from_int(int _vis_value) {
		switch (_vis_value) {
		case 0: // '\0'
			return DAILY;

		case 1: // '\001'
			return WEEKLY;

		case 2: // '\002'
			return MONTHLY;

		case 3: // '\003'
			return QUARTERLY;
		}

		throw new C4RuntimeException("INVALID_EPERIODTYPE in from_int method");
	}

	public String toString() {
		switch (_value) {
		case 0: // '\0'
			return "DAILY";

		case 1: // '\001'
			return "WEEKLY";

		case 2: // '\002'
			return "MONTHLY";

		case 3: // '\003'
			return "QUARTERLY";

		default: // anything else
			return "";

		}

	}

	public boolean equals(java.lang.Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		else
			return (o instanceof EperiodType) ? _value == ((EperiodType) o)._value : false;
	}

}
