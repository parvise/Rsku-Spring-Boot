package com.hp.c4.rsku.rSku.rest.services;

public class MccCalculation {

	public static void main(String[] args) {
//		{MATRL=31.0, DSCPC=20.0, FOREX=4.05, LCP=52.5}

		// (LCP * (1/FOREX) * (1/100))*MATRL% (1-DSPC/100).
		System.out.println((52.5 * (1 / 4.05) * (1 / 100)) * 31.0 * (1 - 20.0 / 100));
		
		 // calculating the cost in local currency
	    double matrl = Double.parseDouble(""+ (31.0 / 100));
	    double discount = Double.parseDouble(""+(20.0 / 100));
	    double price = Double.parseDouble(""+ 52.5);
	    double rate = Double.parseDouble(""+ 4.05);
	    double outputrate = Double.parseDouble(""+ 1.0);

	    // calculating the cost in given currency
	    double cost = matrl * price * (1- discount);

	    // calculating the cost in USD
	    double usdcost = cost;
	    if (rate != 0)
	     usdcost /= rate;

	    double outputcost =  usdcost * outputrate;

	    float res = Float.parseFloat("" + outputcost);
	    System.out.println(res);
	}

}
