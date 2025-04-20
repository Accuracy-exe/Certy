import cv2
import requests
import os
import time as t
from pyzbar.pyzbar import decode
from rich.console import Console
from rich.panel import Panel
from rich.tree import Tree
from rich.prompt import Prompt
from rich import box
from rich.table import Table

cls = lambda : os.system("cls")

console = Console()

VALIDATION_URL = "http://yourserver.com/validate"  # replace with your server

def scan_qr_code():
    cap = cv2.VideoCapture(0)
    console.print("[bold green]Opening camera... Press 'q' to quit.[/bold green]")
    
    qr_data = None

    while True:
        ret, frame = cap.read()
        if not ret:
            console.print("[red]Failed to grab frame from camera.[/red]")
            break

        decoded_objs = decode(frame)
        for obj in decoded_objs:
            qr_data = obj.data.decode("utf-8")
            console.print(f"[yellow]QR Code detected:[/yellow] {qr_data}")
            cap.release()
            cv2.destroyAllWindows()
            return qr_data

        cv2.imshow("Scan QR Code - Press 'q' to quit", frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()
    return None

def validate_certificate(qr_data):
    try:
        response = requests.post(VALIDATION_URL, data=qr_data)
        return response.json().get("status", "invalid")
    except Exception as e:
        console.print(f"[red]Error contacting validation server:[/red] {e}")
        return "invalid"

def show_result(status):
    if status == "valid":
        console.print(Panel.fit("[bold white on green] VALID CERTIFICATE ✅ [/bold white on green]", box=box.DOUBLE))
    else:
        console.print(Panel.fit("[bold white on red] INVALID CERTIFICATE ❌ [/bold white on red]", box=box.DOUBLE))

def main():
    while True:
        cls()
        menu = Tree("MENU")
        menu.add("[violet]Scan QR[/violet]")
        menu.add("[purple]Quit[/purple]")
        console.print(Panel(menu, title="[cyan]Validation Engine[/cyan]", width=30))
        os.system("chafa ./qr_icon.png --size=30x30")
        ch = Prompt.ask("Action", choices=["scan","x"])
        if ch == 'scan':
            qr_data = scan_qr_code()
            if qr_data:
                status = validate_certificate(qr_data)
                show_result(status)
                input("press ENTER to continue")
            else:
                console.print("[red]No QR code scanned.[/red]")
            t.sleep(1)
        if ch == 'x':
            cls()
            break
if __name__ == "__main__":
    main()
