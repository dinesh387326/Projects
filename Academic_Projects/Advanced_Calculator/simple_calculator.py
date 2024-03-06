from stack import Stack

class SimpleCalculator:
    def __init__(self):
        self.history = []
        self.operator_index = 0
    def evaluate_expression(self, in_expression):
        if self.is_valid_expression(in_expression):
            expression = in_expression.replace(" ", "")
            j = self.operator_index
            operands = []
            operands.append(int(expression[0:j]))
            operands.append(int(expression[j+1:]))
            operator = expression[j]
            if operator == '+':
                result = self.add(operands[0], operands[1])
            elif operator == '-':
                result = self.subtract(operands[0], operands[1])
            elif operator == '*':
                result = self.multiply(operands[0], operands[1])
            elif operator == '/':
                result = self.divide(operands[0], operands[1])
            else:
                result = "Error"
        else:
            result = "Error"

        self.history.insert(0, (in_expression, result))
        return result

    def is_valid_expression(self, expression):
        expression = expression.replace(" ", "")
        n = len(expression)
        if n < 3:
            return False
        if expression[0] not in {'+', '-', '*', '/'} and expression[n-1] not in {'+', '-', '*', '/'}:
            for i in range(n):
                if expression[i] in {'+', '-', '*', '/'}:
                    self.operator_index = i
                    break
                if i == n - 1:
                    return False
            if not self.valid_digit(expression[0:self.operator_index]) or not self.valid_digit(expression[self.operator_index + 1:n]):
                return False
            else:
                return True
        else:
            return False

    def valid_digit(self,string):
        for char in string:
            if not ('0' <= char <= '9'):
                return False
        return True

    def add(self, a, b):
        return float(a + b)

    def subtract(self, a, b):
        return float(a - b)

    def multiply(self, a, b):
        return float(a * b)

    def divide(self, a, b):
        if b == 0:
            return "Error"
        return float(a / b)

    def get_history(self):
        return self.history