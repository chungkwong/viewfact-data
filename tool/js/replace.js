var escapeArraySort=function(array){
	array.sort(function(a,b){
		return a[0]<b[0]?-1:(a[0]===b[0]?0:1);
	});
};
var escapeTableLoad=function(file){
	var forward=[];
	var backward=[];
	var request=new XMLHttpRequest();
	request.onload=function(e){
		var lines=request.responseText.split("\n");
		for(var i in lines){
			var line=lines[i];
			var offset=line.indexOf("↔");
			if(offset>=0){
				forward.push([line.substring(0,offset),line.substring(offset+1)]);
				backward.push([line.substring(offset+1),line.substring(0,offset)]);
			}else{
				offset=lines.indexOf("←");
				if(offset>=0){
					backward.push([line.substring(offset+1),line.substring(0,offset)]);
				}else{
					offset=lines.indexOf("→");
					if(offset>=0){
						forward.push([line.substring(0,offset),line.substring(offset+1)]);
					}
				}
			}
		}
	};
	request.open("GET",file);
	request.responseType="text";
	request.send();
	return {"forward":forward,"backward":backward};
};
var escapeTableFromForward=function(forward){
	var backward=[];
	for(var i in forward){
		backward[i]=[forward[i][1],forward[i][0]];
	}
	return {"forward":forward,"backward":backward};
};
var escaper=function(from,to,table){
	escapeArraySort(table.forward);
	escapeArraySort(table.backward);
	var find=function(key,mapping){
		var lower=0;
		var upper=mapping.length-1;
		while(upper>lower){
			var mid=Math.floor((upper+lower)/2);
			if(mapping[mid][0]<key){
				lower=mid+1;
			}else if(mid>0&&mapping[mid-1][0]>=key){
				upper=mid-1;
			}else{
				upper=lower=mid;
			}
		}
		return mapping[lower][0].startsWith(key)?mapping[lower]:null;
	};
	var listen=function(input,output,mapping,postProcessor){
		input.oninput=function(){
			var toConvert=input.value;
			var buf="";
			for(var i=0;i<toConvert.length;i++){
				var j=i+1;
				var key=toConvert.substring(i,j);
				var candidate=find(key,mapping);
				var last=null;
				while(candidate!==null){
					if(candidate[0]===key){
						last=candidate;
					}
					if(++j>toConvert.length){
						break;
					}
					key=toConvert.substring(i,j);
					candidate=find(key,mapping);
				}
				if(last!==null){
					buf+=last[1];
					i+=last[0].length-1;
				}else{
					buf+=toConvert.charAt(i);
				}
			}
			output.value=postProcessor(buf);
		};
	};
	listen(from,to,table.forward,table.forwardPostProcessor!==null?table.forwardPostProcessor:function(str){
		return str;
	});
	listen(to,from,table.backward,table.backwardPostProcessor!==null?table.backwardPostProcessor:function(str){
		return str;
	});
};