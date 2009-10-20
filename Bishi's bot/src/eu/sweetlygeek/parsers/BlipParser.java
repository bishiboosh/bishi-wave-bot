package eu.sweetlygeek.parsers;

import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

/** Interface for all blip parsers
 * @author bishiboosh
 *
 */
public abstract class BlipParser {
	
	public abstract void analyzeBlip(Blip blip, Wavelet currentWavelet);

}
