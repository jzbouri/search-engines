import json
import searchdata
import math


def search(phrase, boost):
    # initializes list that will hold all the results
    all_results = []

    # open the file and extract the dictionary
    with open("crawl_info.json", "r") as file:
        data = json.load(file)

        # create a list and set of words (to filter out duplicates)
        words = phrase.lower().split(" ")
        word_set = set(words)

        # create the query vector
        query_vector = []
        for word in word_set:
            query_vector.append(math.log(1 + words.count(word) / len(words), 2) * searchdata.get_idf(word, data))

        # loop through each url sub-dictionary
        for key, value in data["urls"].items():
            # create the url vector
            url_vector = []
            for word in word_set:
                url_vector.append(searchdata.get_tf_idf(key, word, data))

            # algorithm to calculate cosine similarity
            numerator = 0
            left_denominator = 0
            right_denominator = 0
            for i in range(len(query_vector)):
                numerator += query_vector[i] * url_vector[i]
                left_denominator += query_vector[i] ** 2
                right_denominator += url_vector[i] ** 2
            if numerator != 0:
                left_denominator = math.sqrt(left_denominator)
                right_denominator = math.sqrt(right_denominator)
                cosine_similarity = numerator / (left_denominator * right_denominator)
            else:
                cosine_similarity = 0

            # if boost then multiply cosine similarity by pagerank
            if boost:
                cosine_similarity *= searchdata.get_page_rank(key, data)

            # go through each index of all_results, if the current url score is higher than the score at an index
            # then insert the dictionary at that index, or insert at the end if it isn't higher than any
            for i in range(len(all_results)):
                if all_results[i]["score"] < cosine_similarity:
                    all_results.insert(i, {'url': key, 'title': value['title'], 'score': cosine_similarity})
                    break
            else:
                all_results.append({'url': key, 'title': value['title'], 'score': cosine_similarity})

    # return the top 10 results of all_results
    return all_results[:10]
