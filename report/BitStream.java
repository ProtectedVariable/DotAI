public class BitStream {

	private int position;
	private int bitposition;
	private ByteBuffer buffer;

	public BitStream(ByteBuffer b) {
		this.buffer = b;
		this.position = 0;
		this.bitposition = 0;
	}
	
	public int position() {
		return position;
	}

	public int nextBit() {
		if (position >= buffer.capacity()) {
			throw new IndexOutOfBoundsException("Buffer ended");
		}
		int bit = ((buffer.get(position)) & (1 << (bitposition))) >> bitposition;
		bitposition++;
		if (bitposition == 8) {
			bitposition = 0;
			position++;
		}
		return bit;
	}

	public int readBits(int n) {
		if (n > 32) {
			throw new IllegalArgumentException("Cannot read more than 32 bits at a time !");
		}
		int tmp = 0;
		for (int i = 0; i < n; i++) {
			int t = nextBit();
			tmp |= t << (i);
		}
		return tmp;
	}

	public byte[] readBitsAsBytes(int nbits) {
		byte[] bytes = new byte[(int) Math.round(Math.ceil(nbits / 8.0))];
		int writeIdx = 0;
		for (; nbits >= 8; nbits -= 8) {
			bytes[writeIdx] = (byte) this.readBits(8);
			writeIdx++;
		}
		if (nbits > 0) {
			bytes[writeIdx] = (byte) this.readBits(nbits);
		}
		return bytes;
	}

	public void get(byte[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			if (remaining() > 8) {
				buffer[i] = (byte) readBits(8);
			} else {
				buffer[i] = (byte) readBits(remaining());
			}
		}
	}

	public String readString() {
		StringBuilder sb = new StringBuilder();
		int b = this.readBits(8);
		while (b != 0) {
			sb.append((char) b);
			b = this.readBits(8);
		}
		return sb.toString();
	}

	public int readBitVar() {
		int base = this.readBits(6);
		if ((base & 0x30) == 0x30) {
			base = (base & 0xF) | (this.readBits(28) << 4);
		} else if ((base & 0x30) == 0x20) {
			base = (base & 0xF) | (this.readBits(8) << 4);
		} else if ((base & 0x30) == 0x10) {
			base = (base & 0xF) | (this.readBits(4) << 4);
		}
		return base;
	}

	public int readVarUInt32() {
		int result = 0;
		int position = 0;
		int i = 0;
		do {
			i = this.readBits(8);
			result |= (i & 0x7F) << (position * 7); //remove msb
			position++;
		} while ((i & 0x80) != 0); //while the msb != 0
		return result;
	}
	
	public long readVarUInt64() {
		long result = 0;
		int position = 0;
		int i = 0;
		do {
			i = this.readBits(8);
			result |= (i & 0x7f) << (position * 7);
			position++;
		} while((i & 0x80) != 0);
		return result;
	}
	
	public int readLittleEndian32() {
		byte[] rawdata = new byte[4];
		this.get(rawdata);
		ByteBuffer b = ByteBuffer.wrap(rawdata);
		b.order(ByteOrder.LITTLE_ENDIAN);
		return b.getInt();
	}

	public long readLittleEndian64() {
		byte[] rawdata = new byte[8];
		this.get(rawdata);
		ByteBuffer b = ByteBuffer.wrap(rawdata);
		b.order(ByteOrder.LITTLE_ENDIAN);
		return b.getLong();
	}
	
	public int readFieldPathBitVar() {
		if (nextBit() == 1) return readBits(2);
        if (nextBit() == 1) return readBits(4);
        if (nextBit() == 1) return readBits(10);
        if (nextBit() == 1) return readBits(17);
        return readBits(31);
	}
	
	public float[] read3fNormal() {
		float[] vec = new float[3];
		
		boolean hasX = this.nextBit() == 1;
		boolean hasY = this.nextBit() == 1;
		
		if(hasX) {
			vec[0] = this.readNormal();
		}
		if(hasY) {
			vec[1] = this.readNormal();
		}
		
		float prodSum = vec[0]*vec[0] + vec[1]*vec[1];
		if(prodSum < 1.0f) {
			vec[2] = (float) Math.sqrt(1.0-prodSum);
		} else {
			vec[2] = 0.0f;
		}
		
		//negative z
		if(this.nextBit() == 1) {
			vec[2] = -vec[2];
		}
		return vec;
	}
	
	public float readAngle(int n) {
		return this.readBits(n) * 360.0f / (1 << n);
	}
	
	public float readNormal() {
		boolean isneg = this.nextBit() == 1;
		int len = this.readBits(11);
		float ret = len * (1.0f / ((1 << 1) - 1));
		
		if(isneg) {
			return -ret;
		}
		return ret;
	}
	
	public float readFloat() {
		return Float.intBitsToFloat(this.readLittleEndian32());
	}
	
	public int readVarSInt() {
		 int v = readVarUInt32();
		 return (v >>> 1) ^ -(1 & v);
	}
	
	public float readCoord() {
		float value = 0;
		int intval = this.readBits(1);
		int fractval = this.readBits(1);
		boolean signbit = false;
		if (intval != 0 || fractval != 0) {
			signbit = this.readBits(1) == 1;

			if (intval != 0) {
				intval = this.readBits(14) + 1;
			}

			if (fractval != 0) {
				fractval = this.readBits(5);
			}

			value = intval + fractval * (1.0f / (1 << 5));

			if (signbit) {
				value = -value;
			}
		}
		return value;
	}

	public int remaining() {
		return (buffer.capacity() - position) * 8 - bitposition;
	}

}
