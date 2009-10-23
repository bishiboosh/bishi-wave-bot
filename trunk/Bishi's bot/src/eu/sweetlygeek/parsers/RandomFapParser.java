package eu.sweetlygeek.parsers;

import com.google.wave.api.Wavelet;

public class RandomFapParser extends BlipParser {
	
	private static final String FAP_TAG = "bot:fap";
	
	private static final String GADGET_URL = "http://wave-ide.appspot.com/html.xml";
	
	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		// TODO : mettre le gadget avec la bonne url
	}

	@Override
	protected String getTag() {
		return FAP_TAG;
	}

}
