package HawkClient;

import java.net.URL;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.BaseEncoding;

import java.util.UUID;

public class HawkClient {

	/*
	 * This function generates and returns a random string (nonce).
	 * 
	 * @return The random nonce.
	 * */
	public static String generateNonce() {
		
		return UUID.randomUUID().toString();
	}
	
	/*
	 * This function generates an HTTP Authorization header with Hawk protocol. 
	 * Is possible to generate a header with payload validation.
	 * 
	 * @param url 				request url.
 	 * @param method 			type of HTTP method.
 	 * @param timestamp 		timestamp in seconds.
 	 * @param nonce 			random string.
 	 * @param credentials 		hawk credentials object.
 	 * @param ext Some 			extra string.
 	 * @param payload 			request body.
 	 * @param payloadValidation indicates if the header is created with payload validation.
 	 * 
 	 * @return The Authorization header with Hawk protocol.
	 * */
	public static String createAuthorizationHeader(URL url, String method, String timestamp, String nonce, HawkCredentials credentials, String ext, String payload, Boolean payloadValidation) throws Exception {

		if (url == null || method == null || timestamp == null || nonce == null || credentials == null)
			throw new Exception("url, method, timestamp, nonce and credentials are required!");
		
		// Prepare some variables that may be null.
		payload = (payload == null) ? "" : payload;
	    ext = (ext == null) ? "" : ext;
	    
	    // Generate the payload hash if the request is with payload validation.
	    if (payloadValidation)
	    	payload = generateMAC(payload, credentials);
	    
	    // Generate the MAC.
		String MAC = generateMAC(generateNormalizedString(url, method, timestamp, nonce, credentials, payload, ext), credentials);
		
		// Generate string for Authorization header.
		StringBuilder builder = new StringBuilder();
		builder.append("Hawk id=\"" + credentials.get_identifier() + "\", ")
			   .append("ts=\"" + timestamp + "\", ")
			   .append("nonce=\"" + nonce + "\", ");
		
		if (payloadValidation)
			builder.append("hash=\"" + payload + "\", ");
		
		builder.append("ext=\"" + ext + "\", ")
		   	   .append("mac=\"" + MAC + "\"");
		
		return builder.toString();
	}
	
	/*
	 * This is a private auxiliary function for generate a MAC using SHA256 algorithm.
	 * 
	 * @param normalized 	the normalized string.
 	 * @param credentials 	the user Hawk credentials.
 	 * 
 	 * @return The generated MAC in base64-encoded.
	 * */
	private static String generateMAC(String normalized, HawkCredentials credentials) throws Exception {
		
		String Algorithm = "HmacSHA256";
		Mac mac = Mac.getInstance(Algorithm);
		mac.init(new SecretKeySpec(credentials.get_key().getBytes("UTF-8"), Algorithm));
		return BaseEncoding.base64().encode(mac.doFinal(normalized.getBytes("UTF-8")));
	}
	
	/*
	 * This function generates a normalized string. 
	 * In this function the port number is not used 
	 * because of load balancing performed from some hosts.
	 * 
	 * @param url 			request url.
 	 * @param method 		type of HTTP method.
 	 * @param timestamp 	timestamp in seconds.
 	 * @param nonce 		random string.
 	 * @param credentials 	hawk credentials object.
 	 * @param payload 		request body.
 	 * @param ext Some 		extra string.
 	 * 
 	 * @return the normalized string according to Hawk protocol.
	 * */
	private static String generateNormalizedString(URL url, String method, String timestamp, String nonce, HawkCredentials credentials, String payload, String ext) {
		
		// Preparing the variables.
		String header = "hawk.1.header";
		method = method.toUpperCase();
		String query = (url.getQuery() == null)? "" : "?" + url.getQuery();
		String uri =  url.getPath() + query;
		String host = url.getHost();
		
		// Creating the normalized string.
		StringBuilder builder = new StringBuilder();
		builder.append(header + "\n")
			   .append(timestamp + "\n")
			   .append(nonce + "\n")
			   .append(method + "\n")
			   .append(uri + "\n")
			   .append(host + "\n")
			   .append(payload + "\n")
			   .append(ext + "\n");
		
		return builder.toString();
	}
}