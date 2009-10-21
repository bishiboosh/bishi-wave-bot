package eu.sweetlygeek.parsers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

public class FlickrGetter extends BlipParser {
	
	private static final Logger LOGGER = Logger.getLogger(FlickrGetter.class);
	
	public static final String FLICKR_TAG = "flickr:";
	
	private static final String API_KEY = "969d5e7ab95e1de4299f5f81faf04d1d";
	private static final String SECRET_KEY = "17710a5df2551049";
	
	private static FlickrGetter instance;
	
	private PhotosInterface searcher;
	
	private FlickrGetter()
	{
		Flickr flickr = new Flickr(API_KEY);
		flickr.setSharedSecret(SECRET_KEY);
		this.searcher = flickr.getPhotosInterface();
	}
	
	public static FlickrGetter getInstance()
	{
		if (instance == null)
		{
			instance = new FlickrGetter();
		}
		return instance;
	}

	@Override
	public void analyzeBlip(Blip blip, Wavelet currentWavelet) {
		analyzeBlip(blip, currentWavelet, FLICKR_TAG);
	}

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
				SearchParameters sp = new SearchParameters();
				sp.setTags(new String[] {params[1]});
				sp.setSort(SearchParameters.INTERESTINGNESS_DESC);
				PhotoList list = this.searcher.search(sp, results, 0);
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

}
