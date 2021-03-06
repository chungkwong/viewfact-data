/*
 * Copyright (C) 2018 Chan Chung Kwong
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.chungkwong.viewfact;
import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 * curl -H 'Accept: text/tab-separated-values'
 * 'https://query.wikidata.org/sparql?query=SELECT%20%3Fsite%20%3Furl%20WHERE%20%7B%3Fsite%20wdt%3AP856%20%3Furl.%7D'
 * > wikisite_code.csv cat wikisite_code.csv | sed
 * 's/<http:\/\/www.wikidata.org\/entity\/\(.*\)>\t<\(.*\)>/\1\t\2/' >
 * wikisite_code2.csv
 *
 * @author Chan Chung Kwong
 */
public class WikidataExtracter{
	private static final int LIMIT=50;
	private static final String PREFIX="https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&languages=en|zh&props=aliases|labels|descriptions&ids=";
	public static void main(String[] args) throws IOException{
//		fetchLabels();
		joinLabels();
	}
	private static void joinLabels() throws IOException{
		try(BufferedReader urlIn=Files.newBufferedReader(new File("cache/wikidata/wikisite_code.csv").toPath(),StandardCharsets.UTF_8);
				BufferedReader labelIn=Files.newBufferedReader(new File("cache/wikidata/name_code.csv").toPath(),StandardCharsets.UTF_8);
				BufferedWriter out=Files.newBufferedWriter(new File("cache/wikidata/website.csv").toPath(),StandardCharsets.UTF_8)){
			String urlLine=urlIn.readLine();
			String labelLine=labelIn.readLine();
			out.write("property_id_s\tproperty_title_en_t\tproperty_title_t\tproperty_alias_en_t\tproperty_alias_t\tproperty_description_en_t\tproperty_description_t\tproperty_url_t");
			labelLine=labelIn.readLine();
			while((urlLine=urlIn.readLine())!=null&&labelLine!=null){
				String entityCode=urlLine.substring(0,urlLine.indexOf('\t'));
				if(labelLine.substring(0,labelLine.indexOf('\t')).equals(entityCode)){
					if(labelLine.substring(labelLine.indexOf('\t')).length()>6){
						out.newLine();
						out.write(entityCode);
						out.write(labelLine.substring(labelLine.indexOf('\t')));
						out.write(urlLine.substring(urlLine.indexOf('\t')));
					}
					labelLine=labelIn.readLine();
				}
			}
		}
	}
	private static void fetchLabels() throws IOException{
		try(BufferedReader in=Files.newBufferedReader(new File("cache/wikidata/wikisite_code.csv").toPath(),StandardCharsets.UTF_8);
				BufferedWriter out=Files.newBufferedWriter(new File("cache/wikidata/name_code2.csv").toPath(),StandardCharsets.UTF_8)){
			String line=in.readLine();
			out.write("code\tlabel_en\tlabel_zh\talias_en\talias_zh\tdescription_en\tdescription_zh");
			StringBuilder buf=new StringBuilder(PREFIX);
			int i=0;
			int count=0;
			while((line=in.readLine())!=null){
				String code=line.substring(0,line.indexOf('\t'));
				/*if(count<1034524){
					++count;
				}else */
				if(++i==LIMIT){
					buf.append(code);
					handle(buf.toString(),out);
					System.out.println(count+=LIMIT);
					i=0;
					buf.replace(0,buf.length(),PREFIX);
				}else{
					buf.append(code).append('|');
				}
			}
			if(i>0){
				handle(buf.toString(),out);
			}
		}
	}
	private static void handle(String url,BufferedWriter out) throws IOException{
		Map<String,Map<String,Object>> map;
		while(true){
			try{
				HttpURLConnection connection=(HttpURLConnection)new URL(url).openConnection();
				connection.setConnectTimeout(60000);
				connection.connect();
				int code=connection.getResponseCode();
				while(connection.getHeaderField("Retry-After")!=null){
					connection.disconnect();
					String time=connection.getHeaderField("Retry-After");
					System.err.println(time);
					try{
						try{
							Thread.sleep(Integer.parseInt(time)*1000l);
						}catch(NumberFormatException e){
							Thread.sleep(Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(time)).toEpochMilli()-System.currentTimeMillis());
						}
					}catch(InterruptedException ex){
						ex.printStackTrace();
					}
					connection=(HttpURLConnection)new URL(url).openConnection();
					connection.setConnectTimeout(60000);
					connection.connect();
					code=connection.getResponseCode();
				}
				map=(Map<String,Map<String,Object>>)new ObjectMapper().readValue(connection.getInputStream(),Map.class).get("entities");
				break;
			}catch(Exception ex){
				Logger.getLogger(WikidataExtracter.class.getName()).log(Level.SEVERE,null,ex);
				try{
					Thread.sleep(60000);
				}catch(InterruptedException ex1){
					Logger.getLogger(WikidataExtracter.class.getName()).log(Level.SEVERE,null,ex1);
				}
			}
		}
		for(String entityCode:url.substring(PREFIX.length()).split("\\|")){
			Map<String,Map<String,Object>> entity=(Map<String,Map<String,Object>>)(Map)map.get(entityCode);
			if(entity!=null){
				String labelEn="";
				String aliasEn="";
				String descriptionEn="";
				String labelZh="";
				String aliasZh="";
				String descriptionZh="";
				Map<String,Map<String,String>> labels=(Map<String,Map<String,String>>)(Map)entity.get("labels");
				if(labels!=null){
					if(labels.get("en")!=null&&labels.get("en").get("value")!=null){
						labelEn=labels.get("en").get("value");
					}
					if(labels.get("zh")!=null&&labels.get("zh").get("value")!=null){
						labelZh=labels.get("zh").get("value");
					}
				}
				Map<String,Map<String,String>> descriptions=(Map<String,Map<String,String>>)(Map)entity.get("descriptions");
				if(descriptions!=null){
					if(descriptions.get("en")!=null&&descriptions.get("en").get("value")!=null){
						descriptionEn=descriptions.get("en").get("value");
					}
					if(descriptions.get("zh")!=null&&descriptions.get("zh").get("value")!=null){
						descriptionZh=descriptions.get("zh").get("value");
					}
				}
				Map<String,List<Map<String,String>>> aliases=(Map<String,List<Map<String,String>>>)(Map)entity.get("aliases");
				if(aliases!=null){
					if(aliases.get("en")!=null){
						aliasEn=aliases.get("en").stream().map((m)->m.get("value")).collect(Collectors.joining(";"));
					}
					if(aliases.get("zh")!=null){
						aliasZh=aliases.get("zh").stream().map((m)->m.get("value")).collect(Collectors.joining(";"));
					}
				}
				out.newLine();
				out.write(entityCode);
				out.write('\t');
				out.write(labelEn.replace('\t',' '));
				out.write('\t');
				out.write(labelZh.replace('\t',' '));
				out.write('\t');
				out.write(aliasEn.replace('\t',' '));
				out.write('\t');
				out.write(aliasZh.replace('\t',' '));
				out.write('\t');
				out.write(descriptionEn.replace('\t',' '));
				out.write('\t');
				out.write(descriptionZh.replace('\t',' '));
			}
		}
	}
}
