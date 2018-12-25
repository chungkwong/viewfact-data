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
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.jsoup.*;
/**
 * Please save full
 * https://www.elegislation.gov.hk/index/chapternumber?p0=1&TYPE=1&TYPE=2&TYPE=3&LANGUAGE=C&a0=true
 * to cache/law_tc.html Please save full
 * https://www.elegislation.gov.hk/index/chapternumber?p0=1&TYPE=1&TYPE=2&TYPE=3&LANGUAGE=E&a0=true
 * to cache/law_en.html
 *
 * @author Chan Chung Kwong
 */
public class LawExtracter{
	public static void main(String[] args) throws IOException{
		TreeMap<String,Law> laws=new TreeMap<String,Law>();
		Jsoup.parse(new File("cache/law_en.html"),"UTF-8").select(".hklm_ref").stream().forEach((element)->{
			String link=element.attr("href");
			laws.put(element.attr("href"),new Law(getCode(link),element.text(),link));
		});
		Jsoup.parse(new File("cache/law_tc.html"),"UTF-8").select(".hklm_ref").stream().forEach((element)->{
			Law law=laws.get(element.attr("href"));
			if(law!=null){
				law.setNameTc(element.text());
			}
		});
		StringBuilder buf=new StringBuilder("property_id_s,property_title_t,property_alias_t,property_url_s");
		laws.values().forEach((law)->{
			buf.append('\n').append(encodeField(law.getNo())).append(',').append(encodeField(law.getNameTc())).append(',').append(encodeField(law.getNameEn())).append(',').append(encodeField(law.getLink()));
		});
		Files.write(new File("data/law.csv").toPath(),Collections.singleton(buf.toString()));
	}
	private static String getCode(String link){
		if(link.startsWith("/hk/cap")){
			return link.substring(7);
		}else{
			return link.substring(4);
		}
	}
	private static String encodeField(String field){
		if(field.indexOf(',')!=-1||field.indexOf('"')!=-1||field.indexOf('\n')!=-1){
			return "\""+field.replace("\"","\"\"")+"\"";
		}else{
			return field;
		}
	}
	private static class Law{
		String no, nameEn, nameTc, link;
		public Law(String no,String nameEn,String link){
			this.no=no;
			this.nameEn=nameEn;
			this.nameTc="";
			this.link=link;
		}
		public String getNo(){
			return no;
		}
		public String getNameEn(){
			return nameEn;
		}
		public String getNameTc(){
			return nameTc;
		}
		public String getLink(){
			return link;
		}
		public void setNameTc(String nameTc){
			this.nameTc=nameTc;
		}
	}
}
