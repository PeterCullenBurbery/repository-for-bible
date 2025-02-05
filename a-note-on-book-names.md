Samuel 1 makes more sense than 1 Samuel.
```
"ROW_NUMBER"|"NAME_OF_BOOK"|"BOOK_NUMBER"
"001"|"Samuel 1"|"009"
"002"|"Samuel 2"|"010"
"003"|"Kings 1"|"011"
"004"|"Kings 2"|"012"
"005"|"Chronicles 1"|"013"
"006"|"Chronicles 2"|"014"
"007"|"Corinthians 1"|"046"
"008"|"Corinthians 2"|"047"
"009"|"Thessalonians 1"|"052"
"010"|"Thessalonians 2"|"053"
"011"|"Timothy 1"|"054"
"012"|"Timothy 2"|"055"
"013"|"Peter 1"|"060"
"014"|"Peter 2"|"061"
"015"|"John 1"|"062"
"016"|"John 2"|"063"
"017"|"John 3"|"064"
```
from

```
SELECT
    lpad(ROWNUM, 3, '0')      AS row_number,
    name_of_book,
    lpad(book_number, 3, '0') AS book_number
FROM
    books
WHERE
    REGEXP_LIKE ( name_of_book,
                  '\w+ \d',
                  'i' );
```

There's 17 books that have a number in them. The Old Testament and New Testament have 6 and 11 examples, respectively. Samuel, Kings, Chronicles in OT and Corinthians, Thessalonians, Timothy, Peter, John in New Testament. John has 3, which is interesting. Anyways, I think John 3 makes more sense than 3 John. By a similar argument YYYY-MM-DD makes more sense than DD-MM-YYYY. I would rather know Samuel, Kings, Chronicles, Corinthians, Thessalonians, Timothy, Peter, John then 1 or 2, then 1 or 2, then Samuel, Kings, Chronicles, Corinthians, Thessalonians, Timothy, Peter, John.
