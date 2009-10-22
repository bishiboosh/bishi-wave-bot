package eu.sweetlygeek.parsers;

import com.google.wave.api.Wavelet;

public class RandomFapParser extends BlipParser {
	
	private static final String EMBEDDER_URL = "http://wave-ide.appspot.com/html.xml";
	private static final String FAP_URL = "http://http://www.randomfap.com/";
	private static final String XPATH_EXP = "/html/body/div[3]/div/center";

	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
