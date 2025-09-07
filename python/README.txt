How to run the web crawler and search engine from the command line.
NOTE: These steps work on Mac, may need to adjust for Windows or another OS.

1. Open terminal/command line
2. Navigate to the directory that contains the project contents
3. To perform a web crawl, type the following (replace the link with whatever link you want)
	python3 -c 'import crawler; crawler.crawl("https://people.scs.carleton.ca/~davidmckenney/fruits/N-0.html")'
4. To perform a search, type the following (replace the query with whatever query you want, and adjust the boolean to your liking)
	python3 -c 'import search; search.search("coconut tomato apple peach banana", True)'
NOTE: This will not print out the search results. If you want the results printed, type the following instead
	python3 -c 'import search; print(search.search("coconut tomato apple peach banana", True))'