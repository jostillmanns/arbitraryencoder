package me.tillmanns.arbitraryencoder;

import java.util.HashMap;

class ArbitraryEncoder {
    private static final int BYTESIZE = 8;

    public static void main(String[] args) {

	Character[] encoded = encode("THIS IS A MESSAGE", encodingScheme());

	print(decode(encoded, decodingScheme()));
    }

    public static String decode(Character[] in, HashMap<Character,Character> scheme) {
	String res = "";
	int chunksize = getNextPower(scheme.size());
	int p = 0;
	int idx = 0;
	char c = (char)0;
	int shift;

	while(c != '$') {
	    res = res+c;

	    shift = p % BYTESIZE;
	    if (shift == 0) {
		char mask = (char)(0xFF >>(BYTESIZE - chunksize));

		c = scheme.get((char)(in[idx] & mask));
		p+=chunksize;
		continue;
	    }

	    if (shift+chunksize == BYTESIZE) {
		char mask = (char)(0xFF << shift);
		char r = (char)(in[idx] & mask);
		r = (char)(r >>shift);
		c = scheme.get(r);
		idx++;
		p+=chunksize;
		continue;
	    }

	    if (shift+chunksize < BYTESIZE) {
		char maskRight = (char)(0xFF << shift);
		char maskLeft = (char)(0xFF >> BYTESIZE - (shift+chunksize));
		char mask = (char)(maskRight & maskLeft);

		char r = (char)(in[idx] & mask);
		r = (char)(r >>shift);
		c = scheme.get(r);
		p+=chunksize;
		continue;
	    }

	    if (shift+chunksize > BYTESIZE) {
		char mask = (char)(0xFF << shift);
		char cache = (char)(in[idx] & mask);
		cache = (char)(cache>>shift);

		idx++;
		mask = (char)(0xFF >>(BYTESIZE - (chunksize + shift)));
		char r = (char)(in[idx] & mask);
		r = (char)(r <<(BYTESIZE-shift));
		r = (char)(r | cache);
		c = scheme.get(r);
		p+=chunksize;
		continue;
	    }

	}
	return res;
    }

    public static Character[] encode(String input, HashMap<Character, Character> scheme) {
	input = input+'$';
	char[] in = input.toCharArray();
	int chunksize = getNextPower(scheme.size());
	int p = 0;
	int idx = 0;
	int length = (int)Math.ceil((double)(chunksize*input.length())/(double)BYTESIZE);
	Character[] result = new Character[length];
	Character cache = (char)0;

	for(int i = 0; i < input.length(); i++) {

	    if (p % BYTESIZE == 0) {
		result[idx] = scheme.get(in[i]);
		p+=chunksize;

		continue;
	    }

	    if (p % BYTESIZE != 0) {
		int shift = p % BYTESIZE;
		char ch = scheme.get(in[i]);
		result[idx] = (char)(result[idx] | ch<<shift);

		p+=chunksize;
		if (shift + chunksize == BYTESIZE) {
		    idx++;
		    continue;
		}

		if (shift + chunksize < BYTESIZE) {
		    continue;
		}

		cache = (char)(ch>>(BYTESIZE-shift));
		idx++;

		result[idx] = cache;
		continue;
	    }

	}

	return result;
    }

    private static void print(Object o) {
	System.out.println(o);
    }

    private static HashMap<Character, Character> encodingScheme() {
	HashMap<Character, Character> scheme = new HashMap<Character, Character>();

	scheme.put('$', (char) 0x1E);

	scheme.put('A', (char) 0x00);
	scheme.put('B', (char) 0x01);
	scheme.put('C', (char) 0x02);
	scheme.put('D', (char) 0x03);
	scheme.put('E', (char) 0x04);
	scheme.put('F', (char) 0x05);
	scheme.put('G', (char) 0x06);
	scheme.put('H', (char) 0x07);
	scheme.put('I', (char) 0x08);
	scheme.put('J', (char) 0x09);
	scheme.put('K', (char) 0x0A);
	scheme.put('L', (char) 0x0B);
	scheme.put('M', (char) 0x0C);
	scheme.put('N', (char) 0x0E);
	scheme.put('O', (char) 0x0F);
	scheme.put('P', (char) 0x10);
	scheme.put('Q', (char) 0x11);
	scheme.put('R', (char) 0x12);
	scheme.put('S', (char) 0x13);
	scheme.put('T', (char) 0x14);
	scheme.put('U', (char) 0x15);
	scheme.put('V', (char) 0x16);
	scheme.put('W', (char) 0x17);
	scheme.put('X', (char) 0x18);
	scheme.put('Y', (char) 0x19);
	scheme.put('Z', (char) 0x1A);
	scheme.put(' ', (char) 0x1B);
	scheme.put('{', (char) 0x1C);
	scheme.put('}', (char) 0x1D);

	return scheme;
    }

    private static HashMap<Character, Character> decodingScheme() {
	HashMap<Character, Character> scheme = new HashMap<Character, Character>();
	HashMap<Character, Character> eScheme = encodingScheme();

	for(Character c:eScheme.keySet()) {
	    Character val = eScheme.get(c);
	    scheme.put(val, c);
	}

	return scheme;
    }

    protected static int getNextPower(int x) {
	int i = 1;
	int count = 0;
	while (x > i) {
	    i*=2;
	    count++;
	}
	return count;
    }
}
