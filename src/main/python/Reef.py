import tkinter as tk
from PIL import Image, ImageTk
import math
import os
from networktables import NetworkTables
import time

WINDOW_SIZE = 500
CENTER = WINDOW_SIZE // 2
NUM_SIDES = 6
BUTTON_RADIUS = 180
BUTTON_SPACING = 50

NetworkTables.initialize('127.0.0.1')
if not NetworkTables.isConnected():
    while not NetworkTables.isConnected():
        time.sleep(0.1)
    print('Connected')

table = NetworkTables.getTable("SmartDashboard")

Reef = {
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
    12 : [5.144, 3.687, -60]
}

class ReefGUI:
    def __init__(self, root: tk.Tk):
        global curr_color
        self.root = root
        self.root.title("Reef GUI")
        self.root.geometry(f"{WINDOW_SIZE}x{WINDOW_SIZE}")

        self.canvas = tk.Canvas(root, width=WINDOW_SIZE, height=WINDOW_SIZE, bg="gray")
        self.canvas.pack()

        self.reef_blue = ImageTk.PhotoImage(Image.open("reef_blue.png").resize((150, 150)))

        self.reef_img_obj = self.canvas.create_image(CENTER, CENTER, image=self.reef_blue)

        self.status_label = tk.Label(root, text="", font=("Arial", 12))
        self.status_label.pack(pady=5)

        self.create_paired_buttons()

    def create_paired_buttons(self):
        angle_step = 360 / NUM_SIDES
        start_angle_deg = 90
        button_number = 1

        for i in range(NUM_SIDES):
            angle_deg = start_angle_deg - i * angle_step
            angle_rad = math.radians(angle_deg)

            base_x = CENTER + BUTTON_RADIUS * math.cos(angle_rad)
            base_y = CENTER + BUTTON_RADIUS * math.sin(angle_rad)

            perp_angle_rad = angle_rad + math.pi / 2

            x1 = base_x + (BUTTON_SPACING / 2) * math.cos(perp_angle_rad)
            y1 = base_y + (BUTTON_SPACING / 2) * math.sin(perp_angle_rad)

            x2 = base_x - (BUTTON_SPACING / 2) * math.cos(perp_angle_rad)
            y2 = base_y - (BUTTON_SPACING / 2) * math.sin(perp_angle_rad)

            btn1 = tk.Button(self.root, text=f"{button_number}",
                             command=lambda n=button_number: self.on_button_click(n))
            btn1.place(x=x1 - 25, y=y1 - 15, width=50, height=30)
            button_number += 1

            btn2 = tk.Button(self.root, text=f"{button_number}",
                             command=lambda n=button_number: self.on_button_click(n))
            btn2.place(x=x2 - 25, y=y2 - 15, width=50, height=30)
            button_number += 1

    def on_button_click(self, button_number: int):
        x, y, angle = Reef[button_number]
        table.putNumber("Reef X SetPoint", x)
        table.putNumber("Reef Y SetPoint", y)
        table.putNumber("Reef Angle SetPoint", angle)

def main():
    root = tk.Tk()
    app = ReefGUI(root)
    root.mainloop()

if __name__ == "__main__":
    main()
