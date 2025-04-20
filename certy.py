from rich.panel import Panel
from rich.prompt import Prompt
from rich.tree import Tree
from rich.columns import Columns
from rich.markdown import Markdown
from rich import print
import os

if os.name == 'nt':
    cls = lambda : os.system("cls")
else:
    cls = lambda : os.system("clear")

def renderMainMenu():
    cls()
    menu = Tree("[bold]MENU")
    f = menu.add("[green]Issue (Factory)")
    v = menu.add("[cyan]Validator (Singleton)")
    d = menu.add("[yellow]Database (Adaptor)")
    f.add("Create [violet]New[/violet] Certy")
    v.add("[violet]Val[/violet]idate Certy")
    d.add("Show [violet]Rec[/violet]ords")

    menu_panel = Panel(menu,title="Certy", title_align="right", expand=False)
    md_panel = Panel("Certy is a Digital Certificate Validation tool.\nIt's composed of 3 Microservices. The [red]Issuer[/red]\nis a generator service following Factory pattern,\nthe [red]Validation Engine[/red] runs as a Singleton\nand lastly, the [red]database[/red] communication module\nacts as an adaptor.\nFor more details, [link=https://github.com/Accuracy-exe/Certy]visit our codebase[/link]",
                     title="About", title_align="left", subtitle="made with :heart:  by Arnab, Anurag & Ananya", subtitle_align="right",
                      padding=(0,3))
    views = Columns([menu_panel, md_panel],)
    print(views)

def renderIssue():
    pass

def renderData():
    pass

def renderVal():
    pass 



state = 0

# 0 : Main Menu
# 1 : Issue
# 2 : Validate
# 3 : Database

while True:
    cls()
    if state == 0:
        renderMainMenu()
        a = Prompt.ask("Action", choices=['new','val','rec'])
        if a == 'new':
            state=1
    if state == 1:
        renderIssue()
        a = Prompt.ask("Action")
    if state == 2:
        pass


