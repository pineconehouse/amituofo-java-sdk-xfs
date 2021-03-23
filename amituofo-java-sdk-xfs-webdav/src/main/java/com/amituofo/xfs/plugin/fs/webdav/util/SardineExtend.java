package com.amituofo.xfs.plugin.fs.webdav.util;

import java.net.ProxySelector;

import org.apache.http.impl.client.HttpClientBuilder;

import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemEntryConfig;
import com.github.sardine.impl.SardineImpl;

public class SardineExtend extends SardineImpl {

	public SardineExtend() {
		// TODO Auto-generated constructor stub
	}

	public SardineExtend(String bearerAuth) {
		super(bearerAuth);
		// TODO Auto-generated constructor stub
	}

	public SardineExtend(HttpClientBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	public SardineExtend(String username, String password) {
		super(username, password);
		// TODO Auto-generated constructor stub
	}

	public SardineExtend(String username, String password, ProxySelector selector) {
		super(username, password, selector);
		// TODO Auto-generated constructor stub
	}

	public SardineExtend(HttpClientBuilder builder, String username, String password) {
		super(builder, username, password);
		// TODO Auto-generated constructor stub
	}
	
	public SardineExtend(WebDavFileSystemEntryConfig config)
	{
//		CredentialsProvider credentials=createDefaultCredentialsProvider(config.getUser(), config.getPassword(), null, null);
		MyHttpClientBuilder builder = new MyHttpClientBuilder(config);
		super.client = builder.build();
		
//		ApacheHttpClientBuilder builder = new ApacheHttpClientBuilder(config.getClientConfiguration());
//		super.client = ((ApacheHttpClient)builder.build()).getHttpClient();
	}


}
