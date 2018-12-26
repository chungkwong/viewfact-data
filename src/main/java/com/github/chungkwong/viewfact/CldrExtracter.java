/*
 * Copyright (C) 2018 Chan Chung Kwong
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.chungkwong.viewfact;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class CldrExtracter{
	private static final TreeMap<String,Record> RECORDS=new TreeMap<>();
	public static void main(String[] args) throws IOException{
		Files.newDirectoryStream(new File("cache/unicode/Unihan").toPath()).forEach((path)->{
			try{
				Files.lines(path,StandardCharsets.UTF_8).forEach((line)->{
					if(line.startsWith("U+")){
						int offset=line.indexOf('\t');
						String codePoint=line.substring(2,offset);
						if(!RECORDS.containsKey(codePoint)){
							RECORDS.put(codePoint,new Record(codePoint));
						}
						int offset2=line.indexOf('\t',offset+1);
						String key=line.substring(offset+1,offset2);
						String value=line.substring(offset2+1);
						handleField(key,value,RECORDS.get(codePoint));
					}
				});
			}catch(IOException ex){
				Logger.getLogger(CldrExtracter.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
		try(BufferedWriter out=Files.newBufferedWriter(new File("data/unihan.csv").toPath())){
			out.write("property_id_s,property_title_t,property_remark_t");
			for(Record record:RECORDS.values()){
				out.newLine();
				encode(record.code,out);
				out.write(",");
				encode(record.ch,out);
				out.write(",");
				encode(record.map.entrySet().stream().map((e)->e.getKey()+": "+e.getValue()).collect(Collectors.joining("; ")),out);
			}
		}
	}
	private static void handleField(String key,String value,Record record){
		switch(key){
			case "kAccountingNumeric":
				record.map.put("支票數值",value);
				break;
			case "kCangjie":
				record.map.put("倉頡",value);
				break;
			case "kCantonese":
				record.map.put("粵拼",value);
				break;
			case "kCompatibilityVariant":
				record.map.put("兼容字",getCharacterNames(value));
				break;
			case "kDefinition":
				record.map.put("英文",value);
				break;
			case "kFourCornerCode":
				record.map.put("四角碼",value);
				break;
			case "kFrequency":
				record.map.put("常見程度",value);
				break;
			case "kGradeLevel":
				record.map.put("年級",value);
				break;
			case "kHanyuPinyin":
				record.map.put("漢語拼音",value.substring(value.indexOf(':')+1));
				break;
			case "kMainlandTelegraph":
				record.map.put("電碼",value);
				break;
			case "kMandarin":
				if(!record.map.containsKey("漢語拼音")){
					record.map.put("漢語拼音",value);
				}
				break;
			case "kOtherNumeric":
				record.map.put("其它數值",value);
				break;
			case "kPrimaryNumeric":
				record.map.put("數值",value);
				break;
			case "kRSUnicode":
				record.map.put("部首",getRadicalName(value));
				break;
			case "kSemanticVariant":
				record.map.put("同義",getCharacterNames(value));
				break;
			case "kSimplifiedVariant":
				record.map.put("簡體",getCharacterNames(value));
				break;
			case "kSpecializedSemanticVariant":
				record.map.put("有條件同義",getCharacterNames(value));
				break;
			case "kTaiwanTelegraph":
				record.map.put("台灣電碼",value);
				break;
			case "kTotalStrokes":
				record.map.put("筆畫",value);
				break;
			case "kTraditionalVariant":
				record.map.put("繁體",getCharacterNames(value));
				break;
		}
	}
	private static String getCharacterNames(String code){
		return Arrays.stream(code.split(" ")).map((rs)->{
			int offset=rs.indexOf('<');
			if(offset>=0){
				rs=rs.substring(2,offset);
			}else{
				rs=rs.substring(2);
			}
			return getCharacterName(rs);
		}).collect(Collectors.joining(" "));
	}
	private static String getCharacterName(String code){
		return new String(new int[]{Integer.parseUnsignedInt(code,16)},0,1);
	}
	private static String getRadicalName(String code){
		return Arrays.stream(code.split(" ")).map((rs)->{
			int offset=rs.indexOf('.');
			String radical=rs.substring(0,offset);
			if(radical.endsWith("\'")){
				radical=radical.substring(0,radical.length()-1);
			}
			radical=Character.toString((char)(Integer.parseUnsignedInt(radical)+0x2EFF));
			return radical+rs.substring(offset+1);
		}).collect(Collectors.joining(" "));
	}
	private static void encode(String field,BufferedWriter out) throws IOException{
		if(field.indexOf(',')!=-1||field.indexOf('"')!=-1||field.indexOf('\n')!=-1){
			out.write("\""+field.replace("\"","\"\"")+"\"");
		}else{
			out.write(field);
		}
	}
	private static class Record{
		private final String ch, code;
		private final Map<String,String> map;
		public Record(String code){
			this.code=code;
			this.ch=getCharacterName(code);
			this.map=new LinkedHashMap<>();
		}
	}
}
