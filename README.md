# viewfact-data

Supplement datasets for the search engine viewfact

## Usage

'''SQL
INSERT INTO DATASETS(URL,TITLE,AUTHOR,ACTION,FREQUENCY) VALUES('https://raw.githubusercontent.com/chungkwong/viewfact-data/master/data/index.json','Index of viewfact selected datasets','chungkwong','[{"step":"from-url"},{"step":"to-text"},{"step":"parse-json","path":"$[*]"},{"step":"to-datasets"}]',86400000);
'''

## Notes on the wordnet database

This software and database is being provided to you, the LICENSEE, by
Princeton University under the following license.  By obtaining, using
and/or copying this software and database, you agree that you have
read, understood, and will comply with these terms and conditions.:

Permission to use, copy, modify and distribute this software and
database and its documentation for any purpose and without fee or
royalty is hereby granted, provided that you agree to comply with
the following copyright notice and statements, including the disclaimer,
and that the same appear on ALL copies of the software, database and
documentation, including modifications that you make for internal
use or for distribution.

WordNet 3.1 Copyright 2011 by Princeton University.  All rights reserved.

THIS SOFTWARE AND DATABASE IS PROVIDED "AS IS" AND PRINCETON
UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR
IMPLIED.  BY WAY OF EXAMPLE, BUT NOT LIMITATION, PRINCETON
UNIVERSITY MAKES NO REPRESENTATIONS OR WARRANTIES OF MERCHANT-
ABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT THE USE
OF THE LICENSED SOFTWARE, DATABASE OR DOCUMENTATION WILL NOT
INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR
OTHER RIGHTS.

The name of Princeton University or Princeton may not be used in
advertising or publicity pertaining to distribution of the software
and/or database.  Title to copyright in this software, database and
any associated documentation shall at all times remain with
Princeton University and LICENSEE agrees to preserve same.

# [睇料](https://www.viewfact.org) : 香港人嘅搜尋器

與其它搜尋器如google、yahoo、bing、百度等等相比，睇料有鮮明的特色：

- 釆用盡可能權威和及時的資料來源。我們寧缺勿濫，不是收集盡可能多的網頁實行大包圍，而是堅持聚焦政府和其它信譽良好的來源以保障質素，并會定期自動更新以減少過期資料。
- 優先關注大家身邊的事物。我們重點收集香港本地的資料，因此你可以找到更切身的事物，比如公廁的位置、路況和水上運動課程。
- 樂於轉介。上面兩點決定了我們不擅長於找八卦傳聞、港外資訊或其它相對專門的內容，但我們提供了連結讓你在需要時直接轉到其它搜尋器的結果頁。

因此，在找一些事實型資訊時睇料可能有用，比如：

- 香港地方
    - 街道
    - 樓宇
    - 學校
    - 食肆
    - 食環署轄下設施如街市、公廁、垃圾站
    - 康文署轄下設施如圖書館、公園、體育館
    - 醫管局轄下醫院和門診
    - 回收點
- 香港事件
    - 政府新聞公報
    - 政府舉辦的消閑活動如各類興趣班、演出和展覽
- 香港統計數據
- 全球知名網站
- 實用工具
    - 中英文字典
    - 單位轉換

由於我們現在只有一個IT人一腳踢，從搆思到上線更只用了不到一個月時間，粗糙之處在所難免。
雖然我們夾錢租的server規格可能仲差過你們的手機，太多人用的話未必頂得住，
但我們仍然渴望了解各位試用這個網站后看法，不論是關於結果有用與否、結果太多或太少、
找不到什麽、界面難用與否、反應是否夠快、欠缺什麽功能還是其它方面，以便在不久的將來我們可以改善。
我們歡迎任何批評，就怕被全世界當作透明。感謝大家能讀到這里。