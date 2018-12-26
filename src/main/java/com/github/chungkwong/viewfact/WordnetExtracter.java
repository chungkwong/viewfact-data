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
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import net.sf.extjwnl.*;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.dictionary.Dictionary;
/**
 *
 * @author Chan Chung Kwong
 */
public class WordnetExtracter{
	private static final DecimalFormat idFormat=new DecimalFormat("00000000");
	public static void main(String[] args) throws JWNLException,IOException{
		Dictionary dictionary=Dictionary.getFileBackedInstance("cache/wn3.1.dict/dict");
		try(BufferedWriter out=Files.newBufferedWriter(new File("data/dict_en.csv").toPath())){
			out.write("property_id_s,property_title_t,property_type_t,property_description_t,property_remark_t");
			encode(POS.ADVERB,dictionary,out);
			encode(POS.ADJECTIVE,dictionary,out);
			encode(POS.NOUN,dictionary,out);
			encode(POS.VERB,dictionary,out);
		}
	}
	private static void encode(POS pos,Dictionary dictionary,BufferedWriter out) throws IOException,JWNLException{
		for(Iterator<Exc> iterator=dictionary.getExceptionIterator(pos);iterator.hasNext();){
			Exc next=iterator.next();
			out.newLine();
			out.write(",");
			encode(next.getLemma(),out);
			out.write(",");
			out.write(pos.getLabel());
			out.write(",");
			encode(next.getExceptions().stream().collect(Collectors.joining("; ")),out);
			out.write(",");
		}
		for(Iterator<Synset> iterator=dictionary.getSynsetIterator(pos);iterator.hasNext();){
			Synset next=iterator.next();
			out.newLine();
			out.write(idFormat.format(next.getOffset()));
			out.write(",");
			encode(next.getWords().stream().map((w)->w.getLemma()).collect(Collectors.joining("; ")),out);
			out.write(",");
			out.write(pos.getLabel());
			out.write(",");
			encode(next.getGloss(),out);
			out.write(",");
			String field=next.getPointers().stream().map((p)->resolvePointer(p,dictionary)).collect(Collectors.joining("; "));
			if(next.getPOS()==POS.VERB&&next.getVerbFrames()!=null){
				field+="; Frames: "+Arrays.stream(next.getVerbFrames()).collect(Collectors.joining(", "));
			}
			encode(field,out);
		}
	}
	private static void encode(String field,BufferedWriter out) throws IOException{
		if(field.indexOf(',')!=-1||field.indexOf('"')!=-1||field.indexOf('\n')!=-1){
			out.write("\""+field.replace("\"","\"\"")+"\"");
		}else{
			out.write(field);
		}
	}
	private static String resolvePointer(Pointer p,Dictionary dictionary){
		try{
			Synset synset=p.getTarget().getSynset();
			return p.getType().getLabel()+": "+synset.getWords().stream().map((w)->w.getLemma()).collect(Collectors.joining(", "))+"("+synset.getPOS().getLabel()+", "+idFormat.format(synset.getOffset())+")";
		}catch(JWNLException ex){
			Logger.getLogger(WordnetExtracter.class.getName()).log(Level.SEVERE,null,ex);
			return "";
		}
	}
}
