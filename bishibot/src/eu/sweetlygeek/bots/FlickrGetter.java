package eu.sweetlygeek.bots;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.SearchParameters;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

/** Getter for flickr
 * @author bishiboosh
 *
 */
public class FlickrGetter extends BlipParser {

	private static final Logger LOGGER = Logger.getLogger(FlickrGetter.class);

	public static final String FLICKR_TAG = "flickr:";

	private static final String API_KEY = "969d5e7ab95e1de4299f5f81faf04d1d";
	private static final String SECRET_KEY = "17710a5df2551049";

	private Flickr flickr;

	private String frob;
	private String token;

	public FlickrGetter()
	{
		flickr = new Flickr(API_KEY);
		flickr.setSharedSecret(SECRET_KEY);
		this.frob = null;
		this.token = null;
	}

	private boolean isConnected()
	{
		boolean result = false;
		
		try {
			flickr.getAuthInterface().checkToken(token);
			result = true;
		} catch (IOException e) {
			LOGGER.error("Error while checking connection", e);
		} catch (SAXException e) {
			LOGGER.error("Error while checking connection", e);
		} catch (FlickrException e) {
			result = false;
		}

		return result;
	}

	/** Add a new message to the wavelet asking the user to connect
	 * @param wavelet
	 */
	public void askForConnection(Wavelet wavelet)
	{
		if (!isConnected())
		{
			try {
				AuthInterface ai = flickr.getAuthInterface();
				frob = ai.getFrob();
				URL url = ai.buildAuthenticationUrl(Permission.READ, frob);
				Blip blip = wavelet.appendBlip();
				blip.getDocument().append("Connectez-moi : " + url.toString());
			} catch (MalformedURLException e) {
				LOGGER.error("Error while sending auth URL", e);
			} catch (IOException e) {
				LOGGER.error("Error while sending auth URL", e);
			} catch (SAXException e) {
				LOGGER.error("Error while sending auth URL", e);
			} catch (FlickrException e) {
				LOGGER.error("Error while sending auth URL", e);
			}
		}
	}

	private void auth(Wavelet wavelet)
	{
		try {
			// On v�rifie
			if (token != null)
			{
				// On v�rifie si on est toujours auth
				try {
					flickr.getAuthInterface().checkToken(token);
				} catch (FlickrException e) {
					token = null;
					askForConnection(wavelet);
				}
			}
			else if (frob != null)
			{
				try {
					// On refait l'auth
					Auth token = flickr.getAuthInterface().getToken(frob);
					flickr.setAuth(token);
					this.token = token.getToken();
					flickr.getAuthInterface().checkToken(this.token);
				} catch (FlickrException e) {
					token = null;
					askForConnection(wavelet);
				}
			}
			else
			{
				askForConnection(wavelet);
			}
		} catch (IOException e) {
			LOGGER.error("Error while authenticating", e);
		} catch (SAXException e) {
			LOGGER.error("Error while authenticating", e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.sweetlygeek.parsers.BlipParser#analyzeRequest(java.lang.String, com.google.wave.api.Wavelet)
	 */
	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		try {
			String[] params = request.split(":");
			if (params.length >= 2)
			{
				int results = 10;
				try {
					results = params.length >= 3 ? Integer.parseInt(params[2]) : results;
				} catch (NumberFormatException e) {
					// Nothing
				}
				auth(currentWavelet);
				SearchParameters sp = new SearchParameters();
				sp.setTags(new String[] {params[1]});
				sp.setSort(SearchParameters.INTERESTINGNESS_DESC);
				sp.setSafeSearch(Flickr.SAFETYLEVEL_RESTRICTED);
				PhotoList list = this.flickr.getPhotosInterface().search(sp, results, 0);
				Blip blip = currentWavelet.appendBlip();
				for (Object o : list)
				{
					Photo p = (Photo) o;
					addImage(blip, p.getSmallUrl(), p.getUrl());
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (SAXException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (FlickrException e) {
			LOGGER.error("Error while analyzing request", e);
		}		
	}

	@Override
	public String getTag() {
		return FLICKR_TAG;
	}

}
