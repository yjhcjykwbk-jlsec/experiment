package com.google.zxing.client.result;

import java.text.DecimalFormat;
import java.util.Calendar;

public class LectureParsedResult extends ParsedResult {

	private String subject;
	private String speaker;
	private String starttime;
	private String address;
	private String uri;

	public LectureParsedResult(String subject, String speaker,
			String starttime, String address, String uri) {
		super(ParsedResultType.LECTURE);
		this.subject = subject;
		this.speaker = speaker;
		this.starttime = starttime;
		this.address = address;
		this.uri = uri;
	}

	public String getSubject() {
		return subject;
	}

	public String getSpeaker() {
		return speaker;
	}

	public String getStarttime() {
		return starttime;
	}

	public String getAddress() {
		return address;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public String getDisplayResult() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		int year = Integer.parseInt(starttime.substring(0, 4));
		int month = Integer.parseInt(starttime.substring(4, 6));
		int day = Integer.parseInt(starttime.substring(6, 8));
		int hour = Integer.parseInt(starttime.substring(8, 10));
		int minute = Integer.parseInt(starttime.substring(10));
		DecimalFormat integerFormat = new DecimalFormat("00");
		StringBuilder result = new StringBuilder(100);
		maybeAppend("讲座信息", result);
		maybeAppend("主题：" + subject, result);
		maybeAppend("主讲人：" + speaker, result);
		maybeAppend(
				"开始时间：" + month + "月" + day + "日" + integerFormat.format(hour)
						+ ":" + integerFormat.format(minute), result);
		maybeAppend("地点：" + address, result);
		maybeAppend("链接：" + uri, result);

		return result.toString();
	}

}
