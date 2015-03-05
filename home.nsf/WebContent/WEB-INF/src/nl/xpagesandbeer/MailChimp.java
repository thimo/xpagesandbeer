package nl.xpagesandbeer;

import nl.defrog.RealSimpleMailChimp.MailChimpClient;

import org.json.simple.JSONObject;

public class MailChimp {
	@SuppressWarnings("unchecked")
	public static String subscribe(String email, String name) {
		String retval;
		try {
			final String apikey = Helper.getSetting("mailchimp_apikey");
			MailChimpClient mailChimpClient = MailChimpClient.create(apikey);
			
			String listId = mailChimpClient.getIdForList(Helper.getSetting("mailchimp_default_list"));
			if (listId == null) {
				listId = mailChimpClient.getIdForFirstList();
			}

			JSONObject input = new JSONObject();
			input.put("id", listId);
			input.put("double_optin", false);
			input.put("update_existing", true);
			
			JSONObject emailObj = new JSONObject();
			emailObj.put("email", email);
			input.put("email", emailObj);
			
			String firstname = Helper.getFirstName(name);
			String lastname = Helper.getLastName(name);
			
			JSONObject mergeVars = new JSONObject();
			mergeVars.put("EMAIL", email);
			if (!firstname.equals(""))
				mergeVars.put("FNAME", firstname);
			if (!lastname.equals(""))
				mergeVars.put("LNAME", lastname);
			input.put("merge_vars", mergeVars);
			
			JSONObject response = mailChimpClient.process("lists/subscribe", input);
			retval = response.toJSONString();

		} catch (Exception e) {
			return e.toString();
		}
		return retval;
	}

	// public static void subscribe2(String email) {
	// try {
	// // BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	//
	// String apikey = "536ac5844c7336c3290a0e825d8f1881-us3";
	// System.out.print("Api key: " + apikey);
	//
	// System.out.print("Email: " + email);
	//
	// // reuse the same MailChimpClient object whenever possible
	// MailChimpClient mailChimpClient = new MailChimpClient();
	//
	// ListMethod listMethod = new ListMethod();
	// listMethod.apikey = apikey;
	// Filters listMethodFilters = new Filters();
	// listMethodFilters.list_name = "Aanmeldingen";
	// listMethod.filters = listMethodFilters;
	// ListMethodResult listMethodResult = mailChimpClient.execute(listMethod);
	// System.out.println(listMethodResult.data.size());
	// if (listMethodResult.data.size() == 0) {
	// // No results from search for list name, just get the first one
	// listMethod.filters = null;
	// listMethodResult = mailChimpClient.execute(listMethod);
	// }
	// Data listData = listMethodResult.data.get(0);
	// System.out.println(listData.name);
	// System.out.println(listData.id);
	// String listId = listData.id;
	// System.out.print("List id: " + listId);
	//
	// // Subscribe a person
	// SubscribeMethod subscribeMethod = new SubscribeMethod();
	// subscribeMethod.apikey = apikey;
	// subscribeMethod.id = listId;
	// subscribeMethod.email = new Email();
	// subscribeMethod.email.email = email;
	// subscribeMethod.double_optin = false;
	// subscribeMethod.update_existing = true;
	// subscribeMethod.merge_vars = new MergeVars(email, "Thimo", "Jansen");
	// mailChimpClient.execute(subscribeMethod);
	//
	// System.out.println(email + " has been successfully subscribed to the list. Now will check it...");
	//
	// // check his status
	// MemberInfoMethod memberInfoMethod = new MemberInfoMethod();
	// memberInfoMethod.apikey = apikey;
	// memberInfoMethod.id = listId;
	// memberInfoMethod.emails = Arrays.asList(subscribeMethod.email);
	//
	// MemberInfoResult memberInfoResult = mailChimpClient.execute(memberInfoMethod);
	// MemberInfoData data = memberInfoResult.data.get(0);
	// System.out.println(data.email + "'s status is " + data.status);
	//
	// // Close http-connection when the MailChimpClient object is not needed any longer.
	// // Generally the close method should be called from a "finally" block.
	// mailChimpClient.close();
	// } catch (Exception e) {
	// System.out.println(e.getMessage());
	// e.printStackTrace();
	// } finally {
	// System.out.println("finally");
	// }
	// }
	// public static class MergeVars extends MailChimpObject {
	// private static final long serialVersionUID = -8638022568507049701L;
	//
	// @Field
	// public String EMAIL, FNAME, LNAME;
	//
	// public MergeVars() {
	// }
	//
	// public MergeVars(String email, String fname, String lname) {
	// this.EMAIL = email;
	// this.FNAME = fname;
	// this.LNAME = lname;
	// }
	// }
}
