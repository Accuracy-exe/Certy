import cv2
import requests
import os
import time as t
from pyzbar.pyzbar import decode
from rich import print
from rich.panel import Panel
from rich.tree import Tree
from rich.prompt import Prompt


if os.name == 'nt':
    cls = lambda : os.system("cls")
else:
    cls = lambda : os.system("clear")

menu = Tree("MENU")
menu.add("[violet]Scan QR[/violet]")
menu.add("[purple]Quit[/purple]")


VALIDATION_URL = "http://localhost:8080/validate"  # replace with your server

def scan_qr_code():
    cap = cv2.VideoCapture(0)
    print("[bold green]Opening camera... Press 'q' to quit.[/bold green]")
    
    qr_data = None

    while True:
        ret, frame = cap.read()
        if not ret:
            print("[red]Failed to grab frame from camera.[/red]")
            break

        decoded_objs = decode(frame)
        for obj in decoded_objs:
            qr_data = obj.data.decode("utf-8")
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
        #print(f"[red]Error contacting validation server:[/red] {e}")
        return "invalid"

def show_result(status):
    if status == "valid":
        print(Panel.fit("[bold white on green] VALID CERTIFICATE ✅ [/bold white on green]", style="green" ))
    else:
        print(Panel.fit("[bold white on red] INVALID CERTIFICATE ❌ [/bold white on red]", style="red"))

def main():
    while True:
        cls()
        print(Panel(menu, title="[cyan]Validation Engine[/cyan]", width=30, subtitle="Certy :tulip:", subtitle_align="right"))
        os.system("chafa ./qr_icon.png --size=30x30")
        ch = Prompt.ask("Action", choices=["scan","x"])
        if ch == 'scan':
            qr_data = scan_qr_code()
            if qr_data:
                cls()
                print(Panel(">> QR CODE DETECTED <<",title="[yellow]CAM[/yellow]", title_align="left", style="yellow", expand=False))
                status = validate_certificate(qr_data)
                show_result(status)
                input("press ENTER to continue")
            else:
                print("[red]No QR code scanned.[/red]")
            t.sleep(1)
        if ch == 'x':
            cls()
            break
if __name__ == "__main__":
    main()
