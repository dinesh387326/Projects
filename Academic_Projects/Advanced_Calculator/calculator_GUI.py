import tkinter as tk
from advanced_calculator import AdvancedCalculator  # Make sure your updated file is saved as advanced_calculator.py

class CalculatorGUI:
    def __init__(self, root):
        self.calc = AdvancedCalculator()

        root.title("Scientific Calculator")
        root.geometry("450x600")
        root.resizable(False, False)

        # Expression entry
        self.expression_var = tk.StringVar()
        self.entry = tk.Entry(root, textvariable=self.expression_var, font=("Arial", 18), justify="right", bd=10, relief="sunken")
        self.entry.pack(fill="both", padx=10, pady=10, ipady=8)

        # Result label
        self.result_label = tk.Label(root, text="Result: ", font=("Arial", 16), anchor="w")
        self.result_label.pack(fill="both", padx=10, pady=10)

        # Buttons layout
        buttons = [
            ["7", "8", "9", "/", "sin"],
            ["4", "5", "6", "*", "cos"],
            ["1", "2", "3", "-", "tan"],
            ["0", ".", "(", ")", "+"],
            ["C", "{", "}", "=", "sqrt"],
            ["log", "ln", "exp", "pi", "e"]
        ]

        # Create buttons dynamically
        for row in buttons:
            frame = tk.Frame(root)
            frame.pack(expand=True, fill="both")
            for btn in row:
                action = lambda x=btn: self.on_button_click(x)
                tk.Button(frame, text=btn, font=("Arial", 14), command=action, height=2, width=6)\
                    .pack(side="left", expand=True, fill="both")

    def on_button_click(self, char):
        if char == "C":
            self.expression_var.set("")
            self.result_label.config(text="Result: ")
        elif char == "=":
            expression = self.expression_var.get()
            result = self.calc.evaluate_expression(expression)
            self.result_label.config(text=f"Result: {result}")
        elif char in {"pi", "e"}:
            self.expression_var.set(self.expression_var.get() + char)
        else:
            self.expression_var.set(self.expression_var.get() + str(char))


if __name__ == "__main__":
    root = tk.Tk()
    app = CalculatorGUI(root)
    root.mainloop()
