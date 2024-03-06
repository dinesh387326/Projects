class Stack:
    def __init__(self):
        self.stack = []

    def push(self, item):
        self.stack.append(item)

    def peek(self):
        if self.is_empty():
            return "Error"
        return self.stack[-1]

    def pop(self):
        if self.is_empty():
            return "Error"
        return self.stack.pop()

    def is_empty(self):
        return len(self.stack) == 0

    def __str__(self):
        c = ""
        for i in self.stack:
            c = c + str(i) + " "
        reverse_string = c[::-1]
        return reverse_string[1:]
    def __len__(self):
        return len(self.stack)
