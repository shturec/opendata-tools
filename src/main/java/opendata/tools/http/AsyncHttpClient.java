package opendata.tools.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.util.HttpAsyncClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncHttpClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(AsyncHttpClient.class);

	CloseableHttpAsyncClient httpclient;
	
	public AsyncHttpClient(){
		this.httpclient = HttpAsyncClients.createDefault();
	    httpclient.start();
	}
	
	public interface Callback<T> {
		public void done(T entity);
		public void error(Throwable t);
	}
	
	public void get(String url, final Callback<String> callback) throws IOException{

		try {
		    final CountDownLatch latch = new CountDownLatch(1);
		    final HttpGet request = new HttpGet(url);
		    this.httpclient.execute(request, new FutureCallback<HttpResponse>(){

				@Override
				public void completed(HttpResponse response) {
					latch.countDown();
					if(response.getStatusLine().getStatusCode()>199 && response.getStatusLine().getStatusCode()<400){
						InputStream io = null; 
						try {
							io = response.getEntity().getContent();
							String responseString = IOUtils.toString(io);
							callback.done(responseString);
						} catch (UnsupportedOperationException | IOException e) {
							failed(e);
						} finally {				
							try {
								if(io!=null)
									io.close();
							} catch (IOException e) {
								LOG.warn(e.getMessage(), e);
							}
						}	
					} else {
						LOG.warn("HTTP Request failed: " + response.getStatusLine());
						failed(new Exception("HTTP Request failed: " + response.getStatusLine()));
					}
				}

				@Override
				public void failed(Exception ex) {
					latch.countDown();
					callback.error(ex);
				}

				@Override
				public void cancelled() {
					latch.countDown();
					//TODO: do sth
				}
		    	
		    });
		    try {
				latch.await();
			} catch (InterruptedException e) {}	
		} finally {
		    httpclient.close();
		}
	}
	
	public void shutdown() throws IOException{
		HttpAsyncClientUtils.closeQuietly(this.httpclient);
	}
}
