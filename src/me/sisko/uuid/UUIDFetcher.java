package me.sisko.uuid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Helper-class for getting UUIDs of players
 */
public class UUIDFetcher {

	/**
	 * @param playerName The name of the player
	 * @return The UUID of the given player
	 */
	@SuppressWarnings("StringConcatenationInLoop")
    public static String getUUID(String playerName) {
		String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playerName);

		StringBuilder result = new StringBuilder();

		readData(output, result);

		String u = result.toString();

		String uuid = "";
		try {
			for(int i = 0; i <= 31; i++) {
				uuid += u.charAt(i);
				if(i == 7 || i == 11 || i == 15 || i == 19) {
					uuid += "-";
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			return "notFound";
		}
		return uuid;
	}

	private static void readData(String toRead, StringBuilder result) {
		int i = 7;
		try {
			while(i < 200) {
				if(!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

					result.append(String.valueOf(toRead.charAt(i)));

				} else {
					break;
				}

				i++;
			}
		} catch (StringIndexOutOfBoundsException ignored) {}
	}

	private static String callURL(String URL) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn;
		InputStreamReader in = null;
		try {
			URL url = new URL(URL);
			urlConn = url.openConnection();

			if (urlConn != null) urlConn.setReadTimeout(60 * 1000);

			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);

                int cp;

                while ((cp = bufferedReader.read()) != -1) {
                    sb.append((char) cp);
                }

                bufferedReader.close();
            }

            if (in != null)
                in.close();
        } catch (Exception e) {
			e.printStackTrace();
		} 

		return sb.toString();
	}
}