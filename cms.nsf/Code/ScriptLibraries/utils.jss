var TIMEOUT_LOOKUP_CACHE = (settings.cache_timeout ? settings.cache_timeout : 30 * 60 * 1000 );

/**
 * Returns @DbLookup results as array and allows for cache
 * @param server -name of the server the database is on (only used if dbname not empty, if omitted, the server of the current database is used)
 * @param dbname -name of the database (if omitted the current database is used)
 * @param cache -"cache" for using cache, empty or anything for nocache
 * @param unique -"unique" for returning only unique values, empty or anything for all results
 * @param sortit -"sort" for returning the values sorted alphabetically
 * @param viewname -name of the view
 * @param keyname -key value to use in lookup
 * @param field -field name in the document or column number to retrieve
 * @return array with requested results
 */
//function DbLookupArray(server, dbname, cache, unique, sortit, viewname, keyname, field) {
function dbLookupArray(server, dbname, viewname, keyname, field, params) {
  var cachekey = "dblookup_" + dbname + "_" + @ReplaceSubstring(viewname, " ", "_") + "_" + @ReplaceSubstring(keyname, " ", "_") + "_" + @ReplaceSubstring(field, " ", "_");
  // if cache is specified, try to retrieve the cache from the sessionscope
  var result;
  if (!!params && !!params.cache && params.cache != "false") {
    result = sessionScope.get(cachekey);
    var timestamp = sessionScope.get(cachekey + "_timestamp");
    if (!!result && !!timestamp) {
    	if (timestamp < (Date.now() - (TIMEOUT_LOOKUP_CACHE))) {
    		// Max. time is 15 minutes
    		result = null;
    	}
    }
  }
  // if the result is empty, no cache was available or not requested,
  //    do the dblookup, convert to array if not, cache it when requested
  if (!result) {
    // determine database to run against
    var db = "";
    if (!dbname.equals("")) { // if a database name is passed, build server, dbname array
      if (server.equals("")){
        db = new Array(@DbName()[0],dbname); // no server specified, use server of current database
      } else {
        db = new Array(server, dbname)
      }
    }
    var result = @DbLookup(db, viewname, keyname, field);
    if (result && typeof result == "string") 
    	result = new Array(result);

    if (!!result && !!params) {
	    if (!!params.unique) 
	    	result = @Unique(result);
	    if (!!params.sort) 
	    	result.sort();
	    if (!!params.cache && params.cache != "false") {
	    	sessionScope.put(cachekey, result);
	    	sessionScope.put(cachekey + "_timestamp", Date.now());
	    }
    }
  }
  return result;
}
/**
 * Returns @DbColumn results as array and allows for cache
 * @param server -name of the server the database is on (only used if dbname not empty, if omitted, the server of the current database is used)
 * @param dbname -name of the database (if omitted the current database is used)
 * @param cache -"cache" for using cache, empty or anything for nocache
 * @param unique -"unique" for returning only unique values, empty or anything for all results
 * @param sortit -"sort" for returning the values sorted alphabetically
 * @param viewname -name of the view
 * @param column -column number to retrieve
 * @return array with requested results
 */
function dbColumnArray(server, dbname, viewname, column, params) {
  var cachekey = "dbcolumn_" + dbname + "_" + @ReplaceSubstring(viewname, " ", "_") + "_" + @ReplaceSubstring(column," ","_");
  // if cache is specified, try to retrieve the cache from the sessionscope
  var result;
  if (!!params && !!params.cache && params.cache != "false") {
    result = sessionScope.get(cachekey);
    var timestamp = sessionScope.get(cachekey + "_timestamp");
    if (!!result && !!timestamp) {
    	if (timestamp < (Date.now() - (TIMEOUT_LOOKUP_CACHE))) {
    		// Max. time is 30 minutes
    		result = null;
    	}
    }
  }

  // if the result is empty, no cache was available or not requested,
  //    do the dbcolumn, convert to array if not, cache it when requested
  if (!result) {
    // determine database to run against
    var db = "";
    if (!dbname.equals("")) { // if a database name is passed, build server, dbname array
      if (server.equals("")){
        db = new Array(@DbName()[0],dbname); // no server specified, use server of current database
      } else {
        db = new Array(server, dbname)
      }
    }
    var result = @DbColumn(db, viewname, column);
    if (result && typeof result == "string") 
    	result = new Array(result);
    
    if (!!result && !!params) {
	    if (!!params.unique) 
	    	result = @Unique(result);
	    if (!!params.sort) 
	    	result.sort();
	    if (!!params.cache && params.cache != "false") {
	    	sessionScope.put(cachekey, result);
	    	sessionScope.put(cachekey + "_date", new Date());
	    }
  	}
  }
  return result;
}

function dateFormat(date, format) {
	var df = new java.text.SimpleDateFormat(format);
	if (typeof date == 'lotus.domino.local.DateTime') {
		return df.format(date.toJavaDate());
	} else {
		return df.format(date);
	}
}
