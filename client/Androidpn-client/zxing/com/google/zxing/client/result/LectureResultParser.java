package com.google.zxing.client.result;

import com.google.zxing.Result;

/*
 * The LectureResultParser indicate that if the result is lecture information.
 * The lecture information format is 
 * %LECTURE%&%subject%=...%&%speaker%=...&%starttime%=...&%address%=...&%URI%=...
 */

public final class LectureResultParser extends ResultParser{
	
	public final static String LECTURE = "%LECTURE%";
	public final static String SUBJECT = "&%subject%=";
	public final static String SPEAKER = "&%speaker%=";
	public final static String STARTTIME = "&%starttime%=";
	public final static String ADDRESS = "&%address%=";
	public final static String URI = "&%uri%=";
	
	@Override
	public LectureParsedResult parse(Result theResult) {
		// TODO Auto-generated method stub
		String rawText = getMassagedText(theResult);
		if(!rawText.startsWith(LECTURE)){
			return null;
		}
		
		
		String subject = rawText.substring(rawText.indexOf(SUBJECT) + 11, rawText.indexOf(SPEAKER) - 1);
		String speaker = rawText.substring(rawText.indexOf(SPEAKER) + 11, rawText.indexOf(STARTTIME));
		String starttime = rawText.substring(rawText.indexOf(STARTTIME) + 13, rawText.indexOf(ADDRESS));
		String address = rawText.substring(rawText.indexOf(ADDRESS) + 11, rawText.indexOf(URI));
		String uri = rawText.substring(rawText.indexOf(URI) + 7);
		
		return new LectureParsedResult(subject,speaker,starttime,address,uri);
	}
}
