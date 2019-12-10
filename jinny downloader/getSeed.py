import os
import threading
import time
from csvToObj import csv_to_obj, txt_num_to_obj

parsed_csv = csv_to_obj()
num_downloaded = txt_num_to_obj()
ids = []
num_have = 0
num_not_have = 0
list_down = []
for category in parsed_csv.keys():
    for i in range(len(parsed_csv[category])):
        for id in parsed_csv[category][i].keys():
            if id in num_downloaded.keys():
                parsed_csv[category][i][id]["count"] = num_downloaded[id]
                list_down.append(parsed_csv[category][i][id])
                num_have += 1
            else:
                parsed_csv[category][i][id]["count"] = -1
                num_not_have += 1

list_down.sort(key=lambda x: x["count"], reverse=True)
script_file = open('make_torrent.sh', 'w')
print("#!/bin/sh", file=script_file)
for i in range(len(list_down)):
    command = "ih2torrent " + list_down[i]['magnet'] + ' --file ./torrent/' \
              + str(i) + '.torrent 2> /dev/null > /dev/null'
    print(command, file=script_file)
        # threading.Thread(target=thread_ih2tor, args=(command, ), daemon=True).start()
    # time.sleep(12)
    if i % 500 == 0:
        print(i)
#print(num_have, num_not_have)
pass

#os.system("./rutracker_parser/loader.py --ids_file ./ids_list.txt --thread 1 ")
#print(max(ids))
