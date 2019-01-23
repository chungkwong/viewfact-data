(function () {
	var candidatesPerPage = 10;
	var tables = {};
	var page = [];
	var currentPage = 0;
	var ensureTableLoaded = function (tableFile, callback) {
		if (tables.hasOwnProperty(tableFile)) {
			callback(tables[tableFile]);
		} else {
			var request = new XMLHttpRequest();
			request.onload = function (e) {
				tables[tableFile] = request.responseText.split("\n");
				callback(tables[tableFile]);
			};
			request.onerror = function (e) {
				callback([]);
			};
			request.open("GET", tableFile);
			request.responseType = "text";
			request.send();
		}
	};
	var searchTable = function (code, lines, start, end) {
		var lower = 0;
		var upper = lines.length - 1;
		while (upper > lower) {
			var mid = Math.floor((upper + lower) / 2);
			if (lines[mid] < code) {
				lower = mid + 1;
			} else if (mid > 0 && lines[mid - 1] >= code) {
				upper = mid - 1;
			} else {
				upper = lower = mid;
			}
		}
		return lines.slice(Math.min(lower + start, lines.length), Math.min(lower + end, lines.length));
	};
	var createTableInputMethod = function (keys, keyNames, wildcard, tableFile) {
		keyMap = {};
		for (var i = 0; i < keys.length; i++) {
			keyMap[keys.charAt(i)] = keyNames.charAt(i);
		}
		return {
			"keyMap": keyMap,
			"wildcard": wildcard,
			"search": function (code, pageNumber, callback) {
				ensureTableLoaded(tableFile, function (table) {
					callback(searchTable(code, table, pageNumber * candidatesPerPage, (pageNumber + 1) * candidatesPerPage));
				});
			}
		};
	};
	var inputMethods = {
		wubi: createTableInputMethod("abcdefghijklmnopqrstuvwxy", "abcdefghijklmnopqrstuvwxy", "z", "data/wbx.txt"),
		pinyin: createTableInputMethod("abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyz", "?", "data/pinyin.txt"),
		cantonhk: createTableInputMethod("abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyz", "?", "data/cantonhk.txt"),
		cangjie: createTableInputMethod("abcdefghijklmnopqrstuvwxyz", "日月金木水火土竹戈十大中一弓人心手口尸廿山女田難卜重", "?", "data/cangjie5.txt"),
		zhengma: createTableInputMethod("abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyz", "?", "data/zhengma.txt"),
		english: createTableInputMethod("", "", "", null)
	};
	var getInputMethod = function () {
		return inputMethods[imChooser.value];
	};
	var encodeCandidate = function (line) {
		var parts = line.split(" ");
		var label = parts[1] + " ";
		var method = getInputMethod();
		for (var j = 0; j < parts[0].length; j++) {
			label += method.keyMap[parts[0].charAt(j)];
		}
		return label;
	};
	var area = document.getElementById("imOutput");
	var imCandidates = document.getElementById("imCandidates");
	var imChooser = document.getElementById("imChooser");
	var imInput = document.getElementById("imInput");
	var clearCandidates = function () {
		page = [];
		while (imCandidates.hasChildNodes()) {
			imCandidates.removeChild(imCandidates.lastChild);
		}
	};
	var insertText = function (text) {
		area.setRangeText(text, area.selectionStart, area.selectionEnd, "end");
	};
	var query = function () {
		clearCandidates();
		getInputMethod().search(imInput.value, currentPage, function (data) {
			page = data;
			for (var i = 0; i < page.length; i++) {
				var candidate = document.createElement("li");
				candidate.textContent = encodeCandidate(page[i]);
				imCandidates.appendChild(candidate);
			}
		});
	};
	var choose = function (i, def) {
		if (page.length > i) {
			insertText(page[i].split(" ")[1]);
			imInput.value = "";
		} else {
			insertText(def);
		}
		clearCandidates();
	};
	var previousPage = function () {
		if (currentPage > 0) {
			--currentPage;
			query();
		}
	};
	var nextPage = function () {
		if (page.length > 0) {
			++currentPage;
			query();
		}
	};
	area.onkeypress = function (e) {
		var processed = true;
		if (e.ctrlKey && e.key === "ArrowUp") {
			imChooser.selectedIndex = (imChooser.selectedIndex + imChooser.length - 1) % imChooser.length;
			currentPage = 0;
			query();
		} else if (e.ctrlKey && e.key === "ArrowDown") {
			imChooser.selectedIndex = (imChooser.selectedIndex + 1) % imChooser.length;
			currentPage = 0;
			query();
		} else if (e.code === "Backspace" && imInput.value.length > 0) {
			imInput.value = imInput.value.substring(0, imInput.value.length - 1);
			query();
		} else if (e.code === "Space") {
			choose(0, " ");
			e.stopImmediatePropagation();
			e.preventDefault();
		} else if (e.code === "Enter") {
			insertText(imInput.value === "" ? "\n" : imInput.value);
			imInput.value = "";
		} else if (e.code === "PageUp") {
			previousPage();
		} else if (e.code === "PageDown") {
			nextPage();
		} else if ("0123456789".indexOf(e.key) !== -1) {
			choose(e.key, e.key);
		} else if (getInputMethod().keyMap.hasOwnProperty(e.key) || getInputMethod().wildcard === e.key) {
			imInput.value = imInput.value + e.key;
			currentPage = 0;
			query();
		} else {
			processed = false;
		}
		if (processed) {
			e.stopImmediatePropagation();
			e.preventDefault();
		}
	};
})();