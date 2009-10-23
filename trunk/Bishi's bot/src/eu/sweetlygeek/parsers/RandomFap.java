package eu.sweetlygeek.parsers;

import com.google.wave.api.Blip;
import com.google.wave.api.Gadget;
import com.google.wave.api.Wavelet;

public class RandomFap extends BlipParser {
	
	private static final String FAP_TAG = "bot:fap";
	
	private static final String GADGET_URL = "http://bishibot.appspot.com/_wave/robot/fap.xml";
	
	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		Blip blip = currentWavelet.appendBlip();
		
		Gadget g = new Gadget(GADGET_URL);
		
		blip.getDocument().appendElement(g);
	}

	@Override
	protected String getTag() {
		return FAP_TAG;
	}

}
