package eu.sweetlygeek;

import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

/** Interface for all blip parsers
 * @author bishiboosh
 *
 */
public interface BlipParser {
	
	public void analyzeBlip(Blip blip, Wavelet currentWavelet);

}
