package com.hp.c4.rsku.rSku.security.server.util.icost;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class BaseEncodeDecoder {

	public static void main(String[] args) throws ParseException {

		Encoder theEncoder = Base64.getEncoder();
		String original = "1e60d42d1f3c4fe3ac6df1873d0eb9bc7fd51682f5f96a504b2a1e75f95f20451225883ccaa0cee0bea1d3dae0c395088738c0ef78d4e3180846d0d9fcca6019";
		original="Aq2B!i%XNK9v";
		byte[] theArray = original.getBytes(StandardCharsets.UTF_8);
		String base64encodedString = theEncoder.encodeToString(theArray);
		System.out.println("Original String: " + original);
		System.out.println("Base64 Encoded String : " + base64encodedString);

		Decoder theDecoder = Base64.getDecoder();
		byte[] byteArray = theDecoder.decode("REZFUipDNE9OXzQ1OA==");
		String decoded = new String(byteArray, StandardCharsets.UTF_8);
		System.out.println("decoded String: " + decoded);
		System.out.println("Equal String: " + decoded.equals(original));
		
		//Date myDate = new Date();
		String myDate = "10/01/2021";
		System.out.println(myDate);
		System.out.println(new SimpleDateFormat("yyyy/MM/dd").parse(myDate));
		System.out.println(new SimpleDateFormat("YYYY/MM/DD").parse(myDate));
		System.out.println(new SimpleDateFormat("yyyy-mm-dd").parse(myDate));
		System.out.println(myDate);

		
	}
}
