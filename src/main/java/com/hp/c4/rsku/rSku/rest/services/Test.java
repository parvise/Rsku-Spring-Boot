package com.hp.c4.rsku.rSku.rest.services;

import java.text.DecimalFormat;

public class Test {

	public static void main(String[] args) {

		DecimalFormat decimalFormat = new DecimalFormat("##.00");
		System.out.println(decimalFormat.format(45.7821234));
	}

}
