import tkinter as tk
from tkinter import messagebox
from tkinter.ttk import Treeview, Scrollbar
from tkinter.ttk import Progressbar

from logic import (get_files, search_files, spawn_thread_pool, csv_reader,
                   csv_writer, find_file, class_is_valid, find_bad_classes,
                   get_brand)


class Main(tk.Tk):
    """Displays initial state of GUI."""
   
    def __init__(self, *args, **kwargs):

        tk.Tk.__init__(self, *args, **kwargs)
               
        self.menu = tk.Menu(self)
        self.file_menu = tk.Menu(self.menu, tearoff=False)
        self.file_menu.add_command(label="Find Bad Classes",
                                   command=find_bad_classes)
        self.menu.add_cascade(label="File", menu=self.file_menu)
        self.config(menu=self.menu)

        self.container = tk.Frame(self)
        self.container.grid(column=0, row=0, padx=5, pady=5)
        self.menu_frame = tk.Frame(self.container, borderwidth=1,
                                   relief=tk.RIDGE)
        self.menu_frame.grid(column=0, row=0, padx=10, pady=10)
        self.selections = tk.Frame(self.menu_frame)
        self.selections.grid(column=0, row=1, padx=5, pady=5)
        self.buttons = tk.Frame(self.container)
        self.buttons.grid(column=0, row=2)
        self.treeview = tk.Frame(self.container)
        self.treeview.grid(column=1, row=0)
        self.results = tk.Frame(self.treeview)
        self.results.grid(column=0, row=0)

        tk.Label(self.menu_frame, text="MENU").grid(column=0, row=0,
                                                    sticky="EW")
        tk.Label(self.selections, text="Search:").grid(column=0, row=1,
                                                       sticky="EW")
        self.search_options = ["Part Number", "Part Class",
                               "Part Description", "All"]
        self.search_option = tk.StringVar()
        self.search_option.set("Part Number")
        tk.OptionMenu(self.selections, self.search_option,
                      *self.search_options).grid(column=1, row=1, sticky="EW")
        tk.Label(self.selections, text="Brand:").grid(column=0, row=2,
                                                      sticky="EW")
        self.brands = ["Acer", "Apple", "Asus", "Dell/Alienware",
                       "Hewlett Packard", "Lenovo", "Samsung", "Sony",
                       "Toshiba", "iBuyPower", "MSI", "CyberPowerPC", "Razer",
                       "Canada", "All"]
        self.brand = tk.StringVar()
        self.brand.set("Acer")
        tk.OptionMenu(self.selections, self.brand,
                      *self.brands).grid(column=1, row=2, sticky="EW")
        tk.Label(self.selections, text="Find:").grid(column=0, row=3,
                                                     sticky="EW")
        self.search_term = tk.StringVar()
        self.search_term.set("")
        tk.Entry(self.selections,
                 textvariable=self.search_term).grid(column=1, row=3,
                                                     sticky="EW")
        tk.Button(self.buttons, text="Search",
                  command=self.search_click).grid(column=0, row=1,
                                                  sticky="EW")

    def save_file(self):
        if class_is_valid(self.class_var.get()):
            rows = csv_reader(self.file)
            edited_row = [self.class_var.get(), self.pn_var.get(),
                          self.desc_var.get()]
            edited_rows = []
            for row in rows:
                if self.line_to_edit == row:
                    edited_rows.append(edited_row)
                else:
                    edited_rows.append(row)
            csv_writer(self.file, edited_rows)
            self.edit_win.destroy()
        else:
            tk.messagebox.showerror("Error: Invalid Class",
                                    self.class_var.get() +
                                    " is not a valid part class!")

    def on_double_click(self, event):
        self.edit_win = tk.Toplevel()
        self.selection = self.results_tv.focus()
        print(self.selection)
        self.item_to_edit = self.results_tv.item(self.selection)
        self.file = find_file(self.item_to_edit["text"])
        self.line_to_edit = self.item_to_edit["values"]
        self.class_var = tk.StringVar()
        self.class_var.set(self.line_to_edit[0])
        self.pn_var = tk.StringVar()
        self.pn_var.set(self.line_to_edit[1])
        self.desc_var = tk.StringVar()
        self.desc_var.set(self.line_to_edit[2])

        tk.Label(self.edit_win, text="Class").grid(column=1, row=0, padx=5,
                                                   pady=5)
        tk.Label(self.edit_win, text="Part Number").grid(column=2, row=0,
                                                         padx=5, pady=5)
        tk.Label(self.edit_win, text="Description").grid(column=3, row=0,
                                                         padx=5, pady=5)

        tk.Label(self.edit_win, text="Original:").grid(column=0, row=1)
        original_class = tk.Entry(self.edit_win, relief=tk.FLAT)
        original_class.insert(tk.END, self.line_to_edit[0])
        original_class.grid(column=1, row=1, sticky="EW")
        original_class.config(state="readonly",
                              width=len(self.line_to_edit[0])+3)
        original_pn = tk.Entry(self.edit_win, relief=tk.FLAT)
        original_pn.insert(tk.END, self.line_to_edit[1])
        original_pn.grid(column=2, row=1, sticky="EW")
        original_pn.config(state="readonly",
                           width=len(self.line_to_edit[1])+3)
        original_desc = tk.Entry(self.edit_win, relief=tk.FLAT)
        original_desc.insert(tk.END, self.line_to_edit[2])
        original_desc.grid(column=3, row=1, sticky="EW")
        original_desc.config(state="readonly",
                             width=len(self.line_to_edit[2])+3)

        tk.Label(self.edit_win, text="Edit Here:").grid(column=0, row=2)
        tk.Entry(self.edit_win, textvariable=self.class_var).grid(column=1,
                                                                  row=2,
                                                                  sticky="EW")
        tk.Entry(self.edit_win, textvariable=self.pn_var).grid(column=2,
                                                               row=2,
                                                               sticky="EW")
        tk.Entry(self.edit_win, textvariable=self.desc_var).grid(column=3,
                                                                 row=2,
                                                                 sticky="EW")

        tk.Button(self.edit_win,
                  text="Save",
                  command=self.save_file).grid(column=3, row=3,
                                               padx=5, pady=5)

    def search_click(self):
        files = get_files(get_brand(self.brand.get()))
        results = spawn_thread_pool(files, self.search_term.get(),
                                    self.search_option.get())
        print(results)
        results = [result for result in results if result is not None]
        if results:
            self.results_tv = Treeview(self.treeview,
                                       columns=("Found In",
                                                "Part Class",
                                                "Part Number",
                                                "Part Description"))
            self.results_tv.grid(column=0, row=0)
            vsb = Scrollbar(self.results_tv, orient="vertical",
                            command=self.results_tv.yview)
            vsb.place(x=200, y=300, height=204)
            self.results_tv.heading("#0", text="Found In")
            self.results_tv.heading("#1", text="Part Class")
            self.results_tv.heading("#2", text="Part Number")
            self.results_tv.heading("#3", text="Part Description")
            self.results_tv.column("#1", width=75, anchor=tk.CENTER)
            self.results_tv.column("#3", minwidth=300)
            self.results_tv.column("#4", minwidth=0, width=0)
            for result in results:
                for key, value in result.items():
                    self.results_tv.insert('', tk.END, text=key, values=value)
            self.results_tv.bind("<Double-1>", self.on_double_click)
            tk.Button(self.treeview,
                      text="Save",
                      command=lambda: csv_writer(
                                                 "search results.csv",
                                                 results,
                                                 self.search_term.get()
                                                 )).grid(column=0, row=1)
