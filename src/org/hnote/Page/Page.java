package org.hnote.Page;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ҳ�������
 * @author Zehao Jin
 *
 */
public abstract class Page{
	
	//���bid��category��bname��pattern
	protected String itemPattern;
	
	//����վ��url�����ɾ��������ʵ�������nextURL���������¸�һurl
	protected String urls;
	
	//��17k.com������д�����ݿ��host�ֶ�
	protected String host;
	
	//page content
	protected String content;
	
	//�飬Ϊ<bid,category +'#' + bname>
	private static Map<String,String> books = new HashMap<String,String>();
	

	public Page() {
	}
	
	public abstract String nextURL();
	
	public String getHost(){
		return host;
	}
	
	public static String decode(String str){  
        String[] tmp = str.split("&#|;&#|;");
        StringBuffer sb = new StringBuffer("");  
        for (int i=0; i<tmp.length; i++ ){  
            if (tmp[i].matches("\\d{5}")){  
                sb.append((char)Integer.parseInt(tmp[i]));  
            } else {  
                sb.append(tmp[i]);  
            }  
        }  
        return sb.toString();  
    }
	
	/**
	 * ����ҳ��content
	 * @param is ������
	 */
	public void setContent(InputStream is){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int n = -1;
		try {
			while((n = is.read()) != -1){
				baos.write(n);
			}
			content = baos.toString("utf8");
		}catch(IOException ex){
			System.out.println(ex.getMessage());
		}
	}
	
	/**
	 * ����ҳ�棬���bid�ȡ�
	 */
	public void parsePage(){
		Pattern p = Pattern.compile(itemPattern);
		Matcher m = p.matcher(content);
		Map<String,String> b = new HashMap<String,String>();
		while(m.find()){
			String category = m.group(1);
			if(host.equals("17k.com"))
				category = decode(category);
			String id = m.group(2);
			String name = m.group(3);
			b.put(id, category + '#' + name);
		}
		addBook(b);
	}
	
	protected  void addBook(Map<String,String> book){
		/*
		 * �����������֤books��ȫ
		 */
		synchronized(books){
			books.putAll(book);
		}
		
	}
	
	public Map<String,String> getAndClearBooks(){
		HashMap<String,String> map;
		synchronized(books){
			map = new HashMap<String,String>(books);
			System.out.println(Thread.currentThread().getName() + ": ---------------Get book size :" + map.size() + "------------------");
			books.clear();
		}
		return  map;
	}
	

}