import os

make_rel_path = lambda th_dir, name: th_dir + "/" + name

dir_for_music = "" # Подставить путь


os.chdir(dir_for_music)
dir_list = os.listdir(path=".")
file_list = []
extension_list = set()
need_list = [".flac"]
size = 0

for i in range(len(dir_list)):
    dir_list[i] = dir_for_music + '/' + dir_list[i]


while len(dir_list) > 0:
    if os.path.isdir(dir_list[0]):
        for d in os.listdir(path=dir_list[0]):
            rel = make_rel_path(dir_list[0], d)
            if os.path.isdir(rel):
                dir_list.append(rel)
            elif os.path.isfile(rel) and os.path.splitext(rel)[1] in need_list:
                file_list.append(rel)
    elif os.path.isfile(dir_list[0]) and os.path.splitext(dir_list[0])[1] in need_list:
        file_list.append(dir_list[0])
    dir_list.pop(0)
    #print(len(dir_list), len(file_list))

# print(file_list)

make_convert = lambda flac_name: "ffmpeg -i \"" + flac_name \
                                 + ".flac\" -ab 320k -map_metadata 0 -id3v2_version 3 \"" \
                                 + flac_name + ".mp3\""# >> ./log.txt 2>> ./2log.txt\n"

make_mv = lambda mp3_name: "mv \"" + mp3_name + "\" file_by_ext/flac/mp3_conv/"
make_rm = lambda flac_name: "rm \"" + flac_name + "\""

converter = "#!/bin/sh\n\n"

for i in file_list:
    converter += make_convert(os.path.splitext(i)[0]) + "\n"
    converter += make_mv(os.path.splitext(i)[0] + ".mp3") + "\n"
    converter += make_rm(i) + "\n\n"

bash = open("convert.sh", "w")

print(len(file_list))

print(converter, file=bash)