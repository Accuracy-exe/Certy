import requests
import os
import time as t
from rich import print
from rich.panel import Panel
from rich.tree import Tree
from rich.prompt import Prompt

if os.name == 'nt':
    cls = lambda : os.system("cls")
else:
    cls = lambda : os.system("clear")

menu = Tree("MENU")
menu.add("Issue a Certy")
menu.add("Quit")

data = {
    "inWhat":'',
    "achievement":'',
    "who":'',
    "fromWhere":'',
    "heldAt":'',
    "issuer":'Certy'
}


def renderCertPanel():
    panel = Panel(f"Event: {data['inWhat']}\nAchievement: {data['achievement']}\nIndividual: {data['who']}\nHeld At: {data['heldAt']}", title="Certificate of Achievement", subtitle=f"Issued by {data['issuer']}", subtitle_align="right")
    print(panel)

def main():
    while True:
        cls()
        print(Panel(menu,title="Certy :tulip:"))
        ch = Prompt.ask("Action", choices=["issue","x"])
        if ch == "issue":
            while True:
                cls()
                renderCertPanel()
                opt = Prompt.ask("Edit - [red]e[/red]vent • [red]a[/red]chievement • [red]p[/red]erson • [red]h[/red]eld at • [red]i[/red]ssuer | [red]s[/red]ubmit • [red]q[/red]uit",choices=['e','a','p','h','i','s','q'])
                if opt == 'e':
                    data['inWhat'] = Prompt.ask("Event Name",default="Deep Sleeper's Award")
                if opt == 'a':
                    data['achievement'] = Prompt.ask("Achievement",default="Rank #1")
                if opt == 'p':
                    data['who'] = Prompt.ask("Name of holder",default="Keanu Reeves")
                if opt == 'h':
                    data['heldAt'] = Prompt.ask("Event Venue",default="Their Bed")
                if opt == 'i':
                    data['issuer'] = Prompt.ask("Issued By",default="Certy")
                if opt == 's':
                    cls()
                    res = requests.post("http://localhost:6969/issue", data=data)
                    print("Status Code ", res.status_code)
                    if res.status_code == 200:
                        print(Panel(":tulip: Certy has been Issued :tulip:", style="green"))
                        print("Press [green]ENTER[/green] to continue")
                        input()
                    pass


                if opt == 'q':
                    break
        if ch == 'x':
            cls()
            break

if __name__ == "__main__":
    main()