package org.androidpn.demoapp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

final class GetThread extends Thread {

	private final HttpClient httpClient;
	private final HttpContext context;
	private final HttpPost httppost;
	public HttpResponse httpresponse = null;

	public GetThread(HttpClient httpClient, HttpPost httppost) {
		this.httpClient = httpClient;
		this.context = new BasicHttpContext();
		this.httppost = httppost;
	}

	@Override
	public void run() {
		try {
			httpresponse = this.httpClient.execute(this.httppost, this.context); // post请求的响应
			HttpEntity entity = httpresponse.getEntity();
			if (entity != null) {
				entity.consumeContent(); //finish这个实体
			}
		} catch (Exception ex) {
			this.httppost.abort();
		}
	}
}