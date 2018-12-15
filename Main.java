package pillpack;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// Establish connection to prescriptions list
		String pre_link = "http://api-sandbox.pillpack.com/prescriptions";
		URL pre_url = new URL(pre_link);
		HttpURLConnection pre_connection = (HttpURLConnection) pre_url.openConnection();

		// To obtain information we should use GET method
		pre_connection.setRequestMethod("GET");

		// Get information as input from the connection
		InputStream pre_is = pre_connection.getInputStream();

		// Parse the input to be a JSON array
		JSONParser parser = new JSONParser();
		JSONArray pre_elements = (JSONArray) parser.parse(new InputStreamReader(pre_is, "UTF-8"));

		// This is the final result JSON array that contains only information we need
		// to output to a file (temporarily)
		JSONArray result = new JSONArray();

		// Check size of the original JSON array
		int pre_size = pre_elements.size();

		// A map to store info we have searched in the medication list before to save
		// time for further search
		Map<String, Boolean> med_generic_map = new HashMap<String, Boolean>();
		Map<String, String> med_rxcui_map = new HashMap<String, String>();

		// Handle each object inside JSON array
		for (int i = 0; i < pre_size; i++)
		{
			// Obtain necessary information from each object
			JSONObject obj = (JSONObject) pre_elements.get(i);
			String pre_id = obj.get("id").toString();
			String med_id = obj.get("medication_id").toString();

			// This stores the information about whether it's generic or not
			boolean isGeneric = false;

			// This stores the information about the rxcui number
			String rxcui = "";

			// If this med has not been visited before, make connection to it and
			// add it to med_map for future reference
			if (!med_generic_map.containsKey(med_id))
			{
				// Establish connection to this specific medication
				String id_link = "http://api-sandbox.pillpack.com/medications/" + med_id;
				URL id_url = new URL(id_link);
				HttpURLConnection id_connection = (HttpURLConnection) id_url.openConnection();

				// Obtain information for this specific medication
				InputStream id_is = id_connection.getInputStream();
				JSONObject id_obj = (JSONObject) parser.parse(new InputStreamReader(id_is, "UTF-8"));
				isGeneric = (boolean) id_obj.get("generic");
				rxcui = (String) id_obj.get("rxcui");

				// Add a reference to the map
				med_generic_map.put(med_id, isGeneric);
				med_rxcui_map.put(med_id, rxcui);

				// Close all connections
				id_is.close();
				id_connection.disconnect();
			}
			// Otherwise, just check med_map to see if it's generic
			else
				isGeneric = med_generic_map.get(med_id);

			// Key Reminder: NOT ALL medications have rxcui number

			// If this medication is not generic, see if there is a medication with
			// the same rxcui number that is generic
			if (!isGeneric)
			{
				// Establish connection to all medications that have the same rxcui number
				String rxcui_link = "http://api-sandbox.pillpack.com/medications?rxcui=" + rxcui;
				URL rxcui_url = new URL(rxcui_link);
				HttpURLConnection rxcui_connection = (HttpURLConnection) rxcui_url.openConnection();

				// Obtain information for all medications with the same rxcui number as the
				// current one
				InputStream rxcui_is = rxcui_connection.getInputStream();
				JSONArray rxcui_array = (JSONArray) parser.parse(new InputStreamReader(rxcui_is, "UTF-8"));

				// Go through each element in the array until find the first generic medication
				// Or stop at the end of the array with no probable item found
				for (int j = 0; j < rxcui_array.size(); j++)
				{
					JSONObject current = (JSONObject) rxcui_array.get(j);
					String cur_rxcui = (String) current.get("rxcui");
					if (cur_rxcui.equals(rxcui))
					{
						// Add new prescription id and medication id into
						// a new JSON object and add this object to result array
						String res_id = (String) current.get("id");
						JSONObject res_obj = new JSONObject();
						res_obj.put("prescription_id", pre_id);
						res_obj.put("medication_id", res_id);
						result.add(res_obj);
					}
				}
				
				// Close all connections
				rxcui_is.close();
				rxcui_connection.disconnect();
			}
		}
		
		// Close all original prescription connection
		pre_is.close();
		pre_connection.disconnect();

		// Wrap the array around with a JSON object to give it a key (field index)
		JSONObject wrapper = new JSONObject();
		wrapper.put("prescription_update", result);
		
		// Write JSON object to actual JSON file
		FileWriter fw = new FileWriter("prescription_update.json");
		fw.write(wrapper.toJSONString());
		
		// Close FileWriter
		fw.close();
	}

}
