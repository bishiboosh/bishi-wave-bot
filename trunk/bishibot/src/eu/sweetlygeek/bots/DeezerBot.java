package eu.sweetlygeek.bots;

import com.google.wave.api.Blip;
import com.google.wave.api.Gadget;
import com.google.wave.api.Wavelet;

public class DeezerBot extends BlipParser
{
	private final static String DEEZER_TAG = "bot:deezer";
	private final static String GADGET_URL = "http://bishibot.appspot.com/gadgets/deezer.xml";

	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		Blip b = currentWavelet.appendBlip();
		Gadget g = new Gadget(GADGET_URL);
		b.getDocument().appendElement(g);		
	}

	@Override
	public String getTag() {
		return DEEZER_TAG;
	}
	
}