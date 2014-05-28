package nl.defrog.RealSimpleMailChimp;

//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.net.MalformedURLException;

import nl.defrog.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class MailChimpClient {
	String apikey;
	final static String endpoint_base = "https://us3.api.mailchimp.com/2.0/";
	final static String endpoint_type = "json";

	public static MailChimpClient create(String apikey) {
		MailChimpClient mailChimpClient = new MailChimpClient();

		mailChimpClient.apikey = apikey;

		return mailChimpClient;
	}

	public MailChimpClient() {
	}

	public JSONObject process(String endpoint) {
		JSONObject json = new JSONObject();
		return process(endpoint, json);
	}

	@SuppressWarnings("unchecked")
	public JSONObject process(String endpoint, JSONObject json) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject responseJson = null;

		try {
			json.put("apikey", apikey);

			String endpoint_url = endpoint_base + endpoint + "." + endpoint_type;
			HttpPost postRequest = new HttpPost(endpoint_url);

			System.out.println(json.toJSONString());
			StringEntity input = new StringEntity(json.toJSONString());
			input.setContentType("application/json");
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			responseJson = (JSONObject) JSONValue.parse(Utils.convertStreamToString(response.getEntity().getContent()));

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return responseJson;
	}

	public String getIdForFirstList() {
		return getIdForList(null);
	}

	@SuppressWarnings("unchecked")
	public String getIdForList(String listname) {
		final String endpoint = "lists/list";

		JSONObject input = new JSONObject();

		if (listname != null && !listname.equals("")) {
			JSONObject filters = new JSONObject();
			filters.put("list_name", listname);
			input.put("filters", filters);
		}

		JSONObject response = this.process(endpoint, input);

		JSONArray data = (JSONArray) response.get("data");
		if (data.size() > 0) {
			JSONObject list = (JSONObject) data.get(0);
			return (String) list.get("id");
		} else {
			return null;
		}
	}
}
