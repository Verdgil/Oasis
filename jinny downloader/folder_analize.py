import os

make_rel_path = lambda th_dir, name: th_dir + "/" + name

dir_for_music = "" # Подставить путь

os.chdir(dir_for_music)
dir_list = os.listdir(path=".")
file_list = []
extension_list = set()
need_list = [".flac", ".ogg", ".wma", ".mp3", ".m4a", ".mid"]
size = 0

for i in range(len(dir_list)):
    dir_list[i] = dir_for_music + '/' + dir_list[i]


while len(dir_list) > 0:
    if os.path.isdir(dir_list[0]):
        for d in os.listdir(path=dir_list[0]):
            rel = make_rel_path(dir_list[0], d)
            if os.path.isdir(rel):
                dir_list.append(rel)
            elif os.path.isfile(rel):
                file_list.append(rel)
    elif os.path.isfile(dir_list[0]):
        file_list.append(dir_list[0])
    dir_list.pop(0)
    #print(len(dir_list), len(file_list))


# for ex in file_list:
#     filename, file_extension = os.path.splitext(ex)
#     if file_extension.lower() not in extension_list:
#         print(ex)
#     extension_list.add(file_extension.lower())
#
# for ex in extension_list:
#     print(ex)

# for file in file_list:
#     if os.path.splitext(file)[1] in need_list:
#         size += os.path.getsize(file)
try:
    os.mkdir("file_by_ext")
except:
    pass

for dir_name in need_list:
    try:
        os.mkdir("file_by_ext/" + dir_name[1:])
    except:
        pass

comm = lambda file, ext: "mv \"" + file + "\" file_by_ext/" + ext\
    if "`" not in file else "mv \"" + file.replace("`", "\`") +\
                            "\" file_by_ext/" + ext


for file in file_list:
    if (os.path.splitext(file)[1] in need_list) and ("file_by_ext" not in file):
        print(file)
        try:
            os.system(comm(file, os.path.splitext(file)[1][1:]))
        except:
            pass

#print(size)
