package eu.sweetlygeek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Utils
 * @author bishiboosh
 *
 */
public class Utils {

	private static final Random random = new Random();

	/** Return random values from a value
	 * @param <T> key type
	 * @param <U> value type
	 * @param map
	 * @param nb number of random values
	 * @return map with nb random values of input map
	 */
	public static <T, U> Map<T, U> pickAtRandom(Map<T, U> map, int nb)
	{
		int size = map.size();
		if (size < nb)
		{
			return map;
		}
		else
		{
			Map<T, U> result = new HashMap<T, U>();
			
			List<T> keys = new ArrayList<T>(map.keySet());
			while (result.size() < nb)
			{
				int choice = random.nextInt(size);
				T key = keys.get(choice);
				result.put(key, map.get(key));
			}
			
			return result;
		}
	}

}
