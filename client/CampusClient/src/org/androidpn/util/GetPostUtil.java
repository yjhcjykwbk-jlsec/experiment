package org.androidpn.util;

import java.io.*; 
import java.net.*;
import java.util.*;

import android.util.Log;


public class GetPostUtil
{
	/**
	 * ��ָ��URL����GET����������
	 * 
	 * @param url
	 *            ���������URL
	 * @param params
	 *            ����������������Ӧ����name1=value1&name2=value2����ʽ��
	 * @return URL������Զ����Դ����Ӧ
	 */
	public static String sendGet(String url, String params)
	{
		String result = "";
		BufferedReader in = null;
		try
		{
			String urlName = url + "?" + params;
			URL realUrl = new URL(urlName);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// ����ʵ�ʵ�����
			conn.connect();
			// ��ȡ������Ӧͷ�ֶ�
			Map<String, List<String>> map = conn.getHeaderFields();
			// �������е���Ӧͷ�ֶ�
			for (String key : map.keySet())
			{
				System.out.println(key + "--->" + map.get(key));
			}
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			System.out.println("����GET��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر�������
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * ��ָ��URL����POST����������
	 * 
	 * @param url
	 *            ���������URL
	 * @param params
	 *            ����������������Ӧ����name1=value1&name2=value2����ʽ��
	 * @return URL������Զ����Դ����Ӧ
	 */
	public static String sendPost(String url, String params)
	{
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try
		{
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(conn.getOutputStream());
			// �����������
			out.print(params); // params.toString().getBytes("UTF-8");
			// flush������Ļ���
			out.flush();
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			System.out.println("����POST��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر��������������
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	public static String send(String method, String url, StringBuilder parameter) {
		URL urlObj;
		HttpURLConnection httpConn;
		BufferedReader in = null;
		String result = "";
		try {
			urlObj = new URL(url);
			httpConn = (HttpURLConnection) urlObj.openConnection();
			httpConn.setDoOutput(true); //������HttpUrlConnection���
			httpConn.setDoInput(true); //���ô�HttpUrlConnection����
			httpConn.setRequestMethod(method); //����Http����ʽ��POST��GET

			// �����������
			httpConn.getOutputStream().write(
					parameter.toString().getBytes("GBK"));
			httpConn.getOutputStream().flush();
			httpConn.getOutputStream().close();
			httpConn.getResponseCode();
			result = Integer.toString(httpConn.getResponseCode());

			// ����BufferedReader����������ȡ��Ӧ
			in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ʹ��finally�����ر�������
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
}
