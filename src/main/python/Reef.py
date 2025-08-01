import tkinter as tk
from PIL import Image, ImageTk
from networktables import NetworkTables
import time

WINDOW_SIZE = 800
BUTTON_WIDTH = 40
BUTTON_HEIGHT = 30

NetworkTables.initialize('127.0.0.1')
while not NetworkTables.isConnected():
    time.sleep(0.1)
print("Connected to NetworkTables")
table = NetworkTables.getTable("SmartDashboard")

reef = {
    1 : [4.18, 3.1, 0],
    2 : [3.85, 3.1, 0],
    3 : [2.9, 3.65, 60],
    4 : [2.78, 3.92, 60],
    5 : [2.712, 5.015, 120],
    6 : [2.853, 5.341, 120],
    7 : [3.866, 5.921, 179.5],
    8 : [4.180, 5.934, 179.5],
    9 : [5.168, 5.329, -120],
    10 : [5.37, 5, -120],
    11 : [5.341, 3.946, -60],
    12 : [5.144, 3.687, -60],
    13 : [7, 1.2, -53.70],
    14 : [1.05, 1.1, 55.68],
    15 : [0.5, 6.08, 90],
    16 : [6.5, 7.8, 0]
}

# מיקום הכפתורים לפי פיקסלים יחסיים לתמונה
button_positions = {
    1: (350, 450), 2: (390, 450),
    3: (450, 430), 4: (470, 400),
    5: (470, 340), 6: (430, 310),
    7: (390, 290), 8: (350, 290),
    9: (290, 320), 10: (270, 350),
    11: (270, 390), 12: (280, 420),
    13: (100, 600), 14: (630, 600),
    15: (680, 290), 16: (180, 150)
}

class ReefGUI:
    def __init__(self, root: tk.Tk):
        self.root = root
        self.root.title("Reef GUI")
        self.root.geometry(f"{WINDOW_SIZE}x{WINDOW_SIZE}")

        self.canvas = tk.Canvas(root, width=WINDOW_SIZE, height=WINDOW_SIZE)
        self.canvas.pack()

        image = Image.open("Field.png").resize((WINDOW_SIZE, WINDOW_SIZE))
        self.bg_image = ImageTk.PhotoImage(image)
        self.canvas.create_image(0, 0, anchor="nw", image=self.bg_image)

        self.buttons = {}
        for key, (x, y) in button_positions.items():
            self.create_button(key, x, y)

    def create_button(self, key, x, y):
        btn = tk.Button(self.root, text=str(key), command=lambda k=key: self.on_click(k))
        self.canvas.create_window(x, y, window=btn, width=BUTTON_WIDTH, height=BUTTON_HEIGHT)
        self.buttons[key] = btn

    def on_click(self, key):
        x, y, angle = reef[key]
        table.putNumber("Reef X SetPoint", x)
        table.putNumber("Reef Y SetPoint", y)
        table.putNumber("Reef Angle SetPoint", angle)

def main():
    root = tk.Tk()
    app = ReefGUI(root)
    root.mainloop()

if __name__ == "__main__":
    main()
