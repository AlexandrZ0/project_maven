package com.searchInUrl.project_maven;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class UrlClass {
	
	private int responseCode;
	  private String url;
	  private String html;
	  private String[] splittedHtml;
	  private String delims; 
	  private String excludeFilter;
	  HashMap<Integer, Object[]> recievedStrings ; //сохраним хэшкоды и массив свойств в виде объектов.
	  LinkedList<Integer> lastHashCodeList;
	 UrlClass(String u) {
	   url=u;
	   delims=";|>|<|\"|=|&ldquo;|&quot;|\\{|\\}|--|nbsp;|\'";
	   excludeFilter="(.*)\\d{3}w(.*)|"+
	                "(.*)ID:(.*)|(.*).server(.*)|"+
	                "(.*).column(.*)|(.*)\\(\\)(.*)|"+
	                "(.*)-link(.*)|"+
	                "(.*).data(.*)|(.*).type(.*)|"+
	                "(.*).src(.*)|(.*)function\\s(.*)|"+
	                "(.*).td(.*)|(.*)this.(.*)|"+
	                "(.*)-wrap(.*)|(.*)_analytics(.*)|"+
	                "(.*)-post(.*)|(.*)_slide(.*)|(.*)content-(.*)|"+
	                "(.*)-src(.*)|(.*)opacity:(.*)|(.*)-src(.*)|(.*)-grid(.*)|"+
	                "(.*)Node.(.*)|(.*)WordPress(.*)|(.*)WooCommerce(.*)|"+
	                "(.*)visibility:(.*)|(.*)align:(.*)|(.*)UUID:(.*)|"+
	                "(.*)-icon(.*)|(.*)style\\stype(.*)|(.*)style\\sid(.*)|"+
	                "(.*)link\\srel(.*)|(.*)link\\shref(.*)|(.*)live__(.*)|"+
	                "(.*)charset(.*)|(.*)b\\sclass(.*)|(.*)border:(.*)|"+
	                "(.*)border-(.*)|(.*)\\d{2}\\sJST(.*)|(.*)googletag.(.*)|"+
	                "(.*)typeof(.*)|(.*).push(.*)|(.*)return\\s(.*)|(.*)function\\s(.*)|"+
	                "(.*).call(.*)|(.*).call(.*)|(.*)\\d{2}:\\d{2}(.*)|(.*)\\d{1}:\\d{2}(.*)|"+
	                "(.*).button(.*)|(.*).item(.*)|(.*)-forums(.*)|(.*)b,a,c,g,h(.*)|"+
	                "(.*)ActiveX(.*)|(.*)Bild:(.*)|(.*)column:(.*)|(.*)-id-(.*)|(.*)cache(.*)|"+
	                "(.*)menu_(.*)|(.*):hover(.*)|(.*)-color(.*)|(.*)banner(.*)|(.*)clearfix(.*)|"+
	                "(.*)#box_(.*)|(.*)window.(.*)|(.*)VIEW";
	   recievedStrings=new HashMap<Integer, Object[]>();
	 }
	  // HTTP GET request
	  public String sendGet() {
	        StringBuffer response = new StringBuffer();
	        try {   
	          System.setProperty("http.maxRedirects", "50");
	          URL obj = new URL(url);
	          HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	      
	          // optional default is GET
	          con.setRequestMethod("GET");
	      
	          //add request header
	          con.setRequestProperty("User-Agent", "Mozilla/5.0");
	          con.setConnectTimeout(60000);
	          responseCode = con.getResponseCode();
	          
	          if (responseCode!=200) {
	            System.out.println("Response Code (GET): " + url +":"+responseCode);
	          } else {
	            //
	          }
	          BufferedReader in = new BufferedReader(
	                  new InputStreamReader(con.getInputStream()));
	          String inputLine;
	          while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	          }
	          in.close();
	      } catch (Exception e) {
	          System.out.println("HttpClass raised exception:"+e);
	      }   
	      html=response.toString();
	      splittedHtml=html.split(delims);
	      checkDistinct(splittedHtml);
	      return html;
	  }
	  
	  
	  void setUrl(String u) {
	    url=u;
	  }
	  
	  String getResponse() {
	    return html;
	  }
	  
	  String[] getSplittedHtml() {
	    return splittedHtml;
	  }
	  
	  void checkDistinct(String[] str_arr ) {
	    lastHashCodeList = new LinkedList<Integer>();
	    for (int i=0;i<str_arr.length;i++) {
	     
	      int hc=str_arr[i].hashCode(); //вычислили хэшкод от строки
	      if( recievedStrings.containsKey(hc)) { //если ключик есть в хэшмапе, то я хочу обновить метку времени
	        long unixtime=date2unixtime(new Date());  //получили метку времени
	          recievedStrings.get(hc)[0]=unixtime; //обновляем время
	      } else {//если ключа все-таки нет
	        long unixtime=date2unixtime(new Date());  //получили метку времени
	        Object[] fields=new Object[4];
	        fields[0]=unixtime;
	        fields[1]=str_arr[i];    //обрезок строки.  
	        fields[2]=null; //заглушка под фильтрацию регулярками. я хочу здесь хранить либо -1 если все ок, либо индекс регулярки, которая срезала строку
	        fields[3]=null; //
	        recievedStrings.put(hc,fields);
	        lastHashCodeList.add(hc);
	      }
	    } 
	  }
	  
	  void clearRecievedStrings  (long retentionSeconds) {
	    long unixtime=date2unixtime(new Date());  //получили метку времени
	    for (int hc : recievedStrings.keySet()) {
	      long elementTime=(long)recievedStrings.get(hc)[0];
	      if (elementTime<unixtime-retentionSeconds) {recievedStrings.remove(hc);}
	    }
	  }
	  
	  public void printLatestStrings() {
	    int size=lastHashCodeList.size();
	    System.out.println("In last session we are recieved "+lastHashCodeList.size()+" of new keys");
	    if (size>0) {
	      System.out.println("Let's see them:");
	      for (int hc:lastHashCodeList) System.out.println("'"+(String)recievedStrings.get(hc)[1]+"'");
	    }
	  }
	  
	  public void printSizeOfStringDict() {
	    System.out.println("recievedStrings.size()="+recievedStrings.size());
	  }
	  

	  
	  
	  
	  long date2unixtime(Date d) {
	    return d.getTime() / 1000;
	  }

	 
	}

