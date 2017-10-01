package pv.DotAI.DotAxtractor;

import com.google.protobuf.AbstractMessage;

import pv.DotAI.DotAxtractor.Dem.EDemoCommands;

public class EmbedDataAtom extends Atom {

	private EmbedData data;
	
	public EmbedDataAtom(EDemoCommands type, int tick, int size, AbstractMessage message, EmbedData data) {
		super(type, tick, size, message);
		this.data = data;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
