import tkinter as tk

from modules_python.demo import DemoFrame
from modules_python.theory import TheoryFrame

# ===== APPLICATION =====
class App(tk.Tk):

    def __init__(self):
        super().__init__()
        self.title("Parallelism in Java & Python")
        self.geometry("700x500")
        self.resizable(False, False)

        self.container = tk.Frame(self)
        self.container.pack(fill="both", expand=True)

        self.frames = {}
        frame = MenuFrame(self.container, self)
        self.frames[MenuFrame] = frame
        frame.place(relwidth=1, relheight=1)
        for FrameClass in (TheoryFrame, DecisionFrame, DemoFrame, VisualizationFrame, SyntaxFrame):
            frame = FrameClass(self.container, self, go_home=lambda: self.show(MenuFrame))
            self.frames[FrameClass] = frame
            frame.place(relwidth=1, relheight=1)

        self.show(MenuFrame)

    def show(self, frame_class):
        self.frames[frame_class].lift()


# ===== MENU =====
class MenuFrame(tk.Frame):
    def __init__(self, parent, app):
        super().__init__(parent)
        self.app = app

        tk.Label(self, text="Parallelism in Java & Python", font=("Arial", 22, "bold")).pack(pady=(40, 30))

        btn_frame = tk.Frame(self)
        btn_frame.pack()

        buttons = [
            ("Theory Module", TheoryFrame),
            ("Decision Module", DecisionFrame),
            ("Demo Module", DemoFrame),
            ("Visualization Module", VisualizationFrame),
            ("Syntax Module", SyntaxFrame),
        ]

        for label, target in buttons:
            tk.Button(
                btn_frame, text=label, width=24,
                command=lambda t=target: app.show(t)
            ).pack(pady=5)

        tk.Button(self, text="Exit", command=self.quit).pack(pady=(20, 0))

class BaseFrame(tk.Frame):
    def __init__(self, parent, app, title):
        super().__init__(parent)
        self.app = app

        tk.Label(self, text=title, font=("Arial", 16, "bold")).pack(pady=(20, 10))

        text = tk.Text(self, font=("Courier", 12), state="disabled")
        text.pack(fill="both", expand=True, padx=20, pady=10)

        tk.Button(self, text="Back to Menu",
                  command=lambda: app.show(MenuFrame)).pack(pady=10)

        self._text = text

    def set_content(self, content):
        self._text.config(state="normal")
        self._text.insert("end", content)
        self._text.config(state="disabled")


class DecisionFrame(BaseFrame):
    def __init__(self, parent, app, go_home):
        super().__init__(parent, app, "Decision Module")


class VisualizationFrame(BaseFrame):
    def __init__(self, parent, app, go_home):
        super().__init__(parent, app, "Visualization Module")


class SyntaxFrame(BaseFrame):
    def __init__(self, parent, app, go_home):
        super().__init__(parent, app, "Syntax Module")


# ===== ENTRY POINT =====
if __name__ == "__main__":
    app = App()
    app.mainloop()
