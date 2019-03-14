import datetime as dt
from os import sep, walk
from os.path import join as pathjoin, basename
from csv import writer as csvwriter
from csv import reader as csvreader
from functools import partial
from multiprocessing import Pool


def class_is_valid(part_class):
    CLASSES = ["AC", "AD", "ANT", "AUBD", "AUDIO", "BAT", "BLUE",
                   "BRA", "BRD", "CAB", "CAM", "CARD", "CD", "CMOS",
                   "CORD", "COS", "CRBD", "DCBD", "DCJK", "DOCKIT",
                   "DVD", "FAN", "FDD", "FLD", "GK", "HDD", "HEAT SYNC",
                   "INV", "IOB", "IOP", "KB", "LAN", "LCD", "LDBD", "LK",
                   "MEM", "MIC", "MOD", "OBRD", "OTHER", "PEN", "PROC",
                   "PWBD", "PWR", "REM", "SCRD", "TABMB", "TUBRD", "USBD",
                   "VBRD", "VGBD", "WIR"]
    return part_class in CLASSES


def get_brand(brand):
    BRANDS = {"Acer": "ACE", "Apple": "APP", "Asus": "ASU",
      "Dell/Alienware": "DEL", "Hewlett Packard": "HEW",
      "Lenovo": "LEN", "Samsung": "SAC", "Sony": "SYC",
      "Toshiba": "TSC", "iBuyPower": "IBY", "MSI": "MSS",
      "CyberPowerPC": "CBY", "Razer": "RZR",
      "All": ("ACE", "APP", "ASU", "DEL", "DRC", "HEW",
              "LEN", "SAC", "SYC", "TSC", "IBY", "MSS",
              "CBY")}
    if brand in BRANDS.keys():
        return BRANDS[brand]

    if brand in BRANDS.values():
        for key, value in BRANDS.items():
            if type(value) is tuple:
                if (brand,) in value:
                    return key
            elif brand == value:
                return key


def set_rootdir(brand): 
    base_dir = r"[REDACTED]"
    if brand == "ACE":
        rootdir = pathjoin(base_dir, "Acer_Gateway_Emachine")
    if brand == "APP":
        rootdir = pathjoin(base_dir, "Apple-APP")
    if brand == "ASU":
        rootdir = pathjoin(base_dir, "Asus-ASU")
    if brand in ("DEL", "DRC"):
        rootdir = pathjoin(base_dir, "Dell_Dell Reclamation")
    if brand == "HEW":
        rootdir = pathjoin(base_dir, "HP_Compaq")
    if brand == "LNV":
        rootdir = pathjoin(base_dir, "Lenovo-LEN")
    if brand == "SAC":
        rootdir = pathjoin(base_dir, "Samsung Computer-SAC")
    if brand == "SYC":
        rootdir = pathjoin(base_dir, "Sony Computer-SYC")
    if brand == "TSC":
        rootdir = pathjoin(base_dir, "Toshiba Computer-TSC")
    if brand == "IBY":
        rootdir = pathjoin(base_dir, "iBuyPower-IBY")
    if brand == "MSS":
        rootdir = pathjoin(base_dir, "MSI-MSS")
    if brand == "CBY":
        rootdir = pathjoin(base_dir, "CyberPowerPC-CBP")
    if brand == "RZR":
        rootdir = pathjoin(base_dir, "Razer-RZR")
    if brand == "Canada":
        rootdir = pathjoin(base_dir, "Canada", "Computers")
    if brand == ("ACE", "APP", "ASU", "DEL", "DRC", "HEW",
                 "LEN", "SAC", "SYC", "TSC", "IBY", "MSS",
                 "CBY"):
        rootdir = base_dir
    if brand == "All":
        rootdir = base_dir

    return rootdir


def get_files(brand):
    """
    Set "rootdir" and return list of files to be searched in
    variable "fileslist".

    :param brand: Name of brand
    :return: List of files
    """
    rootdir = set_rootdir(brand)
    fileslist = []

    for subdir, dirs, files in walk(rootdir):
        for file in files:
            file_suffix = file.split("-")[-1][:3]
            if file.endswith(".csv") and file_suffix in brand:
                fileslist.append(subdir + sep + file)

    return fileslist


def fix_nulls(file, *kwargs):
    """Removes null characters from file"""
    for row in file:
        yield row.replace("\0", "")


def csv_writer(file, rows, search_term=""):
    """
    Writes rows to file based on file name.

    :param file: Name to save file as
    :param rows: List of rows to write
    :param search_term: Optional variable so reader remembers what they
        searched for
    """
    with open(file, "w", newline="", encoding="latin1") as csvfile:
        writer = csvwriter(csvfile)

        if basename(file) == "search results.csv":
            writer.writerow(["File Name", "Search Term", "Part Class",
                             "Part Number", "Part Description"])
            for row in rows:
                for key, value in row.items():
                    writer.writerow([key, search_term, value[0], value[1],
                                     value[2]])
        if basename(file).endswith("Bad Classes.csv"):
            writer.writerow(["Model", "Class"])
            writer.writerows(rows)
        else:
            writer.writerows(rows)


def csv_reader(file):
    with open(file, "r", encoding="latin1") as csvfile:
        reader = csvreader(fix_nulls(csvfile), quotechar='"',
                           skipinitialspace=True)
        rows = [row for row in reader]
        return rows


def find_file(file_name):
    brand = file_name.split("-")[-1][:3]
    for file in get_files(brand):
        if file_name in file:
            return pathjoin(file)


def search_files(file_path, search_term="", what_to_search=""):
    search_term = search_term.strip()
    what_to_search = what_to_search.lower().strip()
    parts = csv_reader(file_path)
    for part in parts:
        if len(part) > 2:
            part_dict = {"part class": part[0],
                         "part number": part[1],
                         "part description":part[2]}
            if what_to_search == "all":
                if search_term in ",".join(part):
                    return {basename(file_path): part}
            elif (what_to_search in ["part class", "part number"] and
                    part_dict[what_to_search] == search_term):
                return {basename(file_path): part}
            elif search_term in part_dict[what_to_search]:
                return {basename(file_path): part}
        else:
            continue


def find_bad_classes():
    bad_classes = []
    for file in get_files("All"):
        rows = csv_reader(file)
        for row in rows:
            try:
                if (len(row) > 2 and row[0] != "" and
                        not class_is_valid(row[0])):
                    bad_classes.append([basename(file), row[0]])
                else:
                    continue
            except Exception as e:
                print(file)
                print(row)
                print(str(e))
    if bad_classes:
        csv_writer(str(dt.date.today()) + " Bad Classes.csv", bad_classes)


def spawn_thread_pool(fileslist, term, what):
    pool = Pool(4)
    results = pool.map(partial(search_files, search_term=term,
                       what_to_search=what), fileslist)
    pool.close()
    pool.join()
    return results
