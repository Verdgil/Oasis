import csv

from listNeed import lossy, lossles


def csv_to_obj():
    size_l = 0
    size_ll = 0
    name = ["./csv/category_22", "./csv/category_23", "./csv/category_31", "./csv/category_35", "./csv/category_8"]
    for fname in name:
        csvfile = open(fname + '.csv', 'r')
        fieldnames = ("id_forum", "forum_name", "id", "hash", "name", "size", "date")
        reader = csv.reader(csvfile, delimiter=';')
        #reader =
        json_obj = {}
        for row in reader:
            #{("id_f", "forum_name"): {"id_rzd": {"name": "", "info_hash": "", "size": "", "date": ""}}}
            if ((int(row[0]), row[1]) in lossy) or ((int(row[0]), row[1]) in lossles):
                if ((int(row[0]), row[1]) in lossy):
                    size_l += int(row[5])
                else:
                    size_ll += int(row[5])
                try:
                    json_obj[(int(row[0]), row[1])].append({int(row[2]): {"name": row[4],"hash": row[3], "magnet": "magnet:?xt=urn:btih:" + row[3], "size": int(row[5]), "date": row[6]}})
                except:
                    json_obj[(int(row[0]), row[1])] = [{int(row[2]): {"name": row[4],"hash": row[3], "magnet": "magnet:?xt=urn:btih:" + row[3], "size": int(row[5]), "date": row[6]}}]
        return json_obj


def txt_num_to_obj():
    lines = open("./csv/table_sorted.txt").readlines()
    ret_obj = {}
    for line in lines:
        s = line.split("\t")
        id = int(s[0])
        download_count = int(s[6])
        ret_obj[id] = download_count
    return ret_obj
