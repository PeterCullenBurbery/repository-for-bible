More details about translations can be found in their respective repositories. also details/files, etc.
web was parsed from website at [https://www.gutenberg.org/ebooks/8294.txt.utf-8](https://www.gutenberg.org/ebooks/8294.txt.utf-8).

I built a custom parser. It was hard. That is why I am so eager to provide something that is easier to parse.

Compare 

Book 06 Joshua
001.001 Now it happened after the death of Moses the servant of Yahweh,
        that Yahweh spoke to Joshua the son of Nun, Moses' servant, saying,
001.002 Moses my servant is dead; now therefore arise, go over this Jordan,
        you, and all this people, to the land which I give to them,
        even to the children of Israel.
001.003 I have given you every place that the sole of your foot will
        tread on, as I told Moses.
001.004 From the wilderness, and this Lebanon, even to the great river,
        the river Euphrates, all the land of the Hittites,
        and to the great sea toward the going down of the sun,
        shall be your border.
001.005 No man will be able to stand before you all the days of your life.
        As I was with Moses, so I will be with you.  I will not fail
        you nor forsake you.
001.006 Be strong and of good courage; for you shall cause this
        people to inherit the land which I swore to their fathers
        to give them.

with 

"Joshua"|"001"|"001"|"Now it happened after the death of Moses the servant of Yahweh, that Yahweh spoke to Joshua the son of Nun, Moses' servant, saying,"
"Joshua"|"001"|"002"|"Moses my servant is dead; now therefore arise, go over this Jordan, you, and all this people, to the land which I give to them, even to the children of Israel."
"Joshua"|"001"|"003"|"I have given you every place that the sole of your foot will tread on, as I told Moses."
"Joshua"|"001"|"004"|"From the wilderness, and this Lebanon, even to the great river, the river Euphrates, all the land of the Hittites, and to the great sea toward the going down of the sun, shall be your border."
"Joshua"|"001"|"005"|"No man will be able to stand before you all the days of your life. As I was with Moses, so I will be with you.  I will not fail you nor forsake you."
"Joshua"|"001"|"006"|"Be strong and of good courage; for you shall cause this people to inherit the land which I swore to their fathers to give them."

The latter is parseable, the first is hard. You have to deal with things like verses are on different lines, etc. it sounds easy but there are all sorts of complications and complexities that you have to deal with and account for that you don't think about until you sit down and do it.
