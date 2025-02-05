# bible
I first parsed the web english bible. This is web Peter Burbery because later I found web at https://hub.docker.com/r/rkeplin/bible-mariadb. The other web is web Rob Keplin based on https://www.rkeplin.com/. The docker container https://hub.docker.com/r/rkeplin/bible-mariadb contains a MariaDB/MySQL database with tables like t_niv, t_esv, t_nlt, etc. The tables contain bookId, chapterId, verseId, which contain numbers. bookId is from 1 to 66, Genesis to Revelation. chapterId is the chapter number. verseId is the verse number. verse contains text. The column verse stores the text. I got the data from here and put it in a Oracle database. The tables are table_of_books_of_the_bible, table_of_chapters, table_of_verses with book_id, chapter_id, verse_id, respectively. chapter references book. verse references chapter. table_of_types_of_verses contains types of verses. For example, table_of_types_of_verses contains niv and kjv. table_of_verse_datums contains data in data_of_verse_datum. table_of_verse_datums references verse_id and type_of_verse_id. This table contains 326_295 verses. This table contains the bulk of the data. To get the data you want, use joins. I have views to make accessing the data easy. There is books, chapters, verses. These all provide access to these tables. Then there is aggregate_types, which shows how many verses are in each translation. Then there are the views for translations. There are many of these. The verse views are
  - **esv_peter_burbery**
  - **esv_rob_keplin**
  - **web_peter_burbery**
  - **web_rob_keplin**
  - **niv**
  - **nlt**
  - **kjv**

--major translations
  - **asv**
  - **ylt**
  - **bbe**
  - **wbt**

--minor translations
The data are available in files. The files follow the format

"NAME_OF_BOOK"|"CHAPTER_NUMBER"|"VERSE_NUMBER"|"TEXT_OF_VERSE"
"Genesis"|"001"|"001"|"In the beginning God{After ""God,"" the Hebrew has the two letters ""Aleph Tav"" (the first and last letters of the Hebrew alphabet) as a grammatical marker.} created the heavens and the earth."

where the format is
name of book|chapter number|verse number|text of verse
where chapter number/verse number is zeropadded to 3 places so 123 is unchanged but 12 becomes 012 and 1 becomes 001.
The names are in double quotes so "Genesis"|"001"|"001"|"In the beginning God..." not 'Genesis'|'001'|'001'|'In the beginning God...'. The delimiter is |, not "," comma. DSV is delimiter-separated value, not CSV comma-separated value because | pipe is easier to parse and separate than comma. Comma has issues.

The files are available in export-of-translations.
