import ollama
import subprocess
import shlex
from datetime import datetime

LOGFILE = "/var/log/ollama.log"
MODEL = "llama2"  # nebo codellama/mistral/cokoliv co preferuješ

def log_action(action, command):
    with open(LOGFILE, "a") as log:
        log.write(f"{datetime.now():%F %T} {action}: {command}\n")

def generate_bash_command(instruction):
    prompt = (
        "Jsi linuxový specialista. Převeď tento popis úkolu nebo požadavek na jeden platný bash příkaz bez komentáře:\n"
        f"{instruction}\n"
        "Výstup: pouze konkrétní bash příkaz bez textů navíc."
    )
    resp = ollama.generate(model=MODEL, prompt=prompt, stream=False)
    return resp["response"].strip()

def confirm_and_run(command):
    ans = input(f"Vygenerovaný příkaz: {command}\nSpustit? [y/n]: ").strip().lower()
    if ans in ["y", "a", "yes", "ano"]:
        subprocess.run(shlex.split(command))
        log_action("RUN", command)
    elif ans in ["n", "no", "ne"]:
        print("Příkaz nevykonán, pouze zalogován.")
        log_action("SKIP", command)
    else:
        print("Neplatná odpověď.")
        log_action("INVALID", command)

def main():
    zadani = input("Zadejte popis nebo úkol (např. 'vytvoř adresář /data/backup'): ")
    prikaz = generate_bash_command(zadani)
    confirm_and_run(prikaz)

if __name__ == "__main__":
    main()
