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
		maybeAppend("������Ϣ", result);
		maybeAppend("���⣺" + subject, result);
		maybeAppend("�����ˣ�" + speaker, result);
		maybeAppend(
				"��ʼʱ�䣺" + month + "��" + day + "��" + integerFormat.format(hour)
						+ ":" + integerFormat.format(minute), result);
		maybeAppend("�ص㣺" + address, result);
		maybeAppend("���ӣ�" + uri, result);

		return result.toString();
	}

}
