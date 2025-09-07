import json


def get_outgoing_links(URL):
    with open("crawl_info.json", "r") as file:
        data = json.load(file)
    try:
        return data["urls"][URL]["outgoing_links"]
    except KeyError:
        return None


def get_incoming_links(URL):
    with open("crawl_info.json", "r") as file:
        data = json.load(file)
    try:
        return data["urls"][URL]["incoming_links"]
    except KeyError:
        return None


def get_tf(URL, word):
    with open("crawl_info.json", "r") as file:
        data = json.load(file)
    try:
        tf = data["urls"][URL]["words"][word]["tf"]
        return tf
    except KeyError:
        return 0


# the next three functions use an optional argument which is used by the search function to significantly reduce the
# amount of times the file is opened and closed
def get_idf(word, dic=None):
    if dic is not None:
        try:
            tfidf = dic["word_idfs"][word]
            return tfidf
        except KeyError:
            return 0
    with open("crawl_info.json", "r") as file:
        data = json.load(file)
    try:
        idf = data["word_idfs"][word]
        return idf
    except KeyError:
        return 0


def get_tf_idf(URL, word, dic=None):
    if dic is not None:
        try:
            tfidf = dic["urls"][URL]["words"][word]["tfidf"]
            return tfidf
        except KeyError:
            return 0
    with open("crawl_info.json", "r") as file:
        data = json.load(file)
    try:
        tfidf = data["urls"][URL]["words"][word]["tfidf"]
        return tfidf
    except KeyError:
        return 0


def get_page_rank(URL, dic=None):
    if dic is not None:
        try:
            return dic["urls"][URL]["page_rank"]
        except KeyError:
            return -1
    with open("crawl_info.json", "r") as file:
        data = json.load(file)
    try:
        return data["urls"][URL]["page_rank"]
    except KeyError:
        return -1
