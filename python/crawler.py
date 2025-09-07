import json
import math
from webdev import read_url

# function that creates a new url from a href link (either relative or absolute)
def create_url(line, url):
    if "http" in line:
        return line.split('href="./')[1].split('">')[0]
    else:
        return url.rpartition("/")[0] + line.split('href=".')[1].split('"')[0]


# recursive function that collects data from urls and it's outgoing links
def parse(url):
    # set default values, create url list, add unique index to the urls sub-dictionary, and append thr url to the
    # visited urls list
    is_words = False
    words = []
    url_string = read_url(url)
    url_string_list = url_string.splitlines()
    crawl_dict["urls"][url]["index"] = len(visited_urls)
    visited_urls.append(url)

    # parse each line of the html
    for line in url_string_list:
        # find and create title, and update sub-dictionary
        if "<title>" in line:
            title = line.split("title>")[1][:-2]
            crawl_dict["urls"][url]["title"] = title

        # algorithm for collecting word data, updating word document count, and uploading each word idf to the url
        # sub-dictionary
        if "</p>" in line:
            line_words = line.split("</p>")[0].split(" ")
            [words.append(word.strip()) for word in line_words if word != ""]
            is_words = False
            crawl_dict["urls"][url].setdefault("words", {})
            for word in words:
                crawl_dict["urls"][url]["words"].setdefault(word, {})
                crawl_dict["urls"][url]["words"][word]["tf"] = crawl_dict["urls"][url]["words"][word].setdefault("tf",
                                                                                                                 0) + 1
            for word in set(words):
                crawl_dict["word_idfs"][word] = crawl_dict["word_idfs"].setdefault(word, 0) + 1
                crawl_dict["urls"][url]["words"][word]["tf"] = crawl_dict["urls"][url]["words"][word]["tf"] / len(words)
        if is_words:
            line_words = line.split(" ")
            [words.append(word.strip()) for word in line_words]
        if "<p" in line:
            is_words = True
            line_words = line.split("<p")[1].split(">")[1].split(" ")
            [words.append(word.strip()) for word in line_words if word != ""]

        # algorithm that updates incoming/outgoing links for url sub-dictionaries, and calls recursive function
        if "href" in line:
            new_url = create_url(line, url)
            crawl_dict["urls"][url].setdefault("outgoing_links", [])
            if new_url not in crawl_dict["urls"][url]["outgoing_links"]:
                crawl_dict["urls"][url]["outgoing_links"].append(new_url)
            crawl_dict["urls"].setdefault(new_url, {})
            crawl_dict["urls"][new_url].setdefault("incoming_links", [])
            if new_url not in crawl_dict["urls"][new_url]["incoming_links"]:
                crawl_dict["urls"][new_url]["incoming_links"].append(url)
            if new_url not in visited_urls:
                parse(new_url)


# function to calculate euclidian distance, used in the pagerank function
def get_euclidean_dist(a, b):
    distance = 0
    for i in range(len(a)):
        distance += (a[i] - b[i]) ** 2
    distance = math.sqrt(distance)
    return distance


# function to create probability vectors, used in the pagerank function
def create_t2(t1, adjacency_matrix):
    result = []
    for i in range(len(adjacency_matrix[0])):
        new_value = 0
        for j in range(len(t1)):
            new_value += t1[j] * adjacency_matrix[j][i]
        result.append(new_value)
    return result


# function that calculates pagerank for all urls, and stores them in the url's sub-dictionary
def get_page_ranks():
    length = len(visited_urls)

    adjacency_matrix = []
    for value in crawl_dict["urls"].values():
        row = [0] * length
        for link in value["outgoing_links"]:
            row[crawl_dict["urls"][link]["index"]] = 1
        row = [(num / row.count(1) * 0.9) + (0.1 / length) for num in row]
        adjacency_matrix.append(row)
    for value in crawl_dict["urls"].values():
        del value["index"]

    t1 = [1 / length] * length
    t2 = create_t2(t1, adjacency_matrix)
    distance = get_euclidean_dist(t1, t2)

    while distance > 0.0001:
        t1, t2 = t2, create_t2(t2, adjacency_matrix)
        distance = get_euclidean_dist(t1, t2)

    for i in range(len(visited_urls)):
        crawl_dict["urls"][visited_urls[i]]["page_rank"] = t2[i]


# function that calculates idf for all words found on all websites, and stores them in the dictionary
def get_idfs():
    for key, value in crawl_dict["word_idfs"].items():
        crawl_dict["word_idfs"][key] = math.log(((len(visited_urls)) / (1 + value)), 2)


# function that calculates tfidf for all all sets of words in each URL, and stores them in the dictionary
def get_tfidfs():
    for value in crawl_dict["urls"].values():
        for word, dic in value["words"].items():
            value["words"][word]["tfidf"] = math.log((1 + dic["tf"]), 2) * crawl_dict["word_idfs"][word]


# main crawl function that calls all other functions and uploads to a file when all functions have been executed
def crawl(seed):
    global crawl_dict, visited_urls
    # visited_urls will store the urls of pages that have already been visited, crawl_dict will store all data collected
    visited_urls = []
    crawl_dict = {"word_idfs": {}, "urls": {seed: {}}}

    # call recursive parsing function on the seed
    parse(seed)

    # call functions to calculate page ranks, idfs, and tfidfs
    get_page_ranks()
    get_idfs()
    get_tfidfs()

    # upload the dictionary to a json file
    with open("crawl_info.json", "w") as file:
        json.dump(crawl_dict, file, indent=4)

    # return the total number
    return len(visited_urls)
