import math
from simple_calculator import SimpleCalculator
from stack import Stack


class AdvancedCalculator(SimpleCalculator):
    def calculate(self, operator, op1=None, op2=None):
        """Handles binary (+, -, *, /) and unary (sin, cos, log, etc.) operations."""
        try:
            # Binary operators
            if operator == "+":
                return round(float(op2 + op1), 10)
            elif operator == "-":
                return round(float(op2 - op1), 10)
            elif operator == "*":
                return round(float(op2 * op1), 10)
            elif operator == "/":
                if op1 == 0:
                    return "Error"
                return round(float(op2 / op1), 10)

            # Unary operators (single operand)
            elif operator == "sin":
                return round(math.sin(math.radians(op1)), 10)
            elif operator == "cos":
                return round(math.cos(math.radians(op1)), 10)
            elif operator == "tan":
                return round(math.tan(math.radians(op1)), 10)
            elif operator == "log":
                return round(math.log10(op1), 10)
            elif operator == "ln":
                return round(math.log(op1), 10)
            elif operator == "sqrt":
                return round(math.sqrt(op1), 10)
            elif operator == "exp":
                return round(math.exp(op1), 10)

            return "Error"
        except Exception:
            return "Error"

    def evaluate_expression(self, in_expression):
        list_tokens = self.tokenize(in_expression)
        if not self.check_brackets(list_tokens):
            result = "Error"
            self.history.insert(0, (in_expression, result))
            return result
        if not self.is_valid_expression(in_expression):
            result = "Error"
            self.history.insert(0, (in_expression, result))
            return result
        result = self.evaluate_list_tokens(list_tokens)
        self.history.insert(0, (in_expression, result))
        return result

    def is_valid_expression(self, input_expression):
        """Validate expression by checking token sequence, operators, and operands."""
        input_expression = input_expression.replace(" ", "")
        if not input_expression:
            return False

        tokens = self.tokenize(input_expression)
        if not tokens:
            return False

        prev = None
        for i, token in enumerate(tokens):
            # Function check
            if token in {"sin", "cos", "tan", "log", "ln", "sqrt", "exp"}:
                if i == len(tokens) - 1 or tokens[i+1] not in {"(", "{"}:
                    return False

            # Operator check
            elif token in {"+", "-", "*", "/"}:
                if i == 0 or i == len(tokens) - 1:
                    return False
                if tokens[i+1] in {"+", "-", "*", "/", ")", "}"}:
                    return False
                if prev in {"+", "-", "*", "/", "(", "{"} or prev is None:
                    return False

            # Operand (number or constant)
            elif isinstance(token, (int, float)):
                if prev is not None and isinstance(prev, (int, float)):
                    return False

            prev = token

        return True

    def tokenize(self, input_expression):
        """Splits numbers, operators, brackets, constants, and functions into tokens."""
        input_expression = input_expression.replace(" ", "")
        tokens = []
        current_token = ""

        i = 0
        while i < len(input_expression):
            char = input_expression[i]

            # Numbers (including decimals)
            if char.isdigit() or char == ".":
                current_token += char

            # Constants
            elif input_expression[i:i+2] == "pi":
                if current_token:
                    tokens.append(float(current_token))
                    current_token = ""
                tokens.append(math.pi)
                i += 1
            elif char == "e":
                if current_token:
                    tokens.append(float(current_token))
                    current_token = ""
                tokens.append(math.e)

            # Functions
            elif char.isalpha():
                if current_token:
                    tokens.append(float(current_token))
                    current_token = ""
                func_name = ""
                while i < len(input_expression) and input_expression[i].isalpha():
                    func_name += input_expression[i]
                    i += 1
                tokens.append(func_name)
                continue  # already advanced i

            # Operators/brackets
            elif char in ("+", "-", "*", "/", "(", ")", "{", "}"):
                if current_token:
                    tokens.append(float(current_token))
                    current_token = ""
                tokens.append(char)

            i += 1

        if current_token:
            tokens.append(float(current_token))

        return tokens

    def check_brackets(self, list_tokens):
        stacks = Stack()
        for token in list_tokens:
            if token in ("(", "{"):
                previous_element = stacks.peek()
                stacks.push(token)
                if len(stacks) >= 2:
                    if (token == '{') and (previous_element == '('):
                        return False
            elif token in (")", "}"):
                if stacks.is_empty():
                    return False
                opening_bracket = stacks.pop()
                if (opening_bracket == "(" and token != ")") or (opening_bracket == "{" and token != "}"):
                    return False

        return stacks.is_empty()

    def evaluate_list_tokens(self, list_tokens):
        operand_stack = Stack()
        operator_stack = Stack()

        precedence = {
            "+": 1, "-": 1,
            "*": 2, "/": 2,
            "sin": 3, "cos": 3, "tan": 3,
            "log": 3, "ln": 3,
            "sqrt": 3, "exp": 3
        }

        for token in list_tokens:
            if isinstance(token, (int, float)):
                operand_stack.push(token)

            elif token in precedence:
                while (not operator_stack.is_empty() and
                       operator_stack.peek() in precedence and
                       precedence[token] <= precedence[operator_stack.peek()]):
                    op = operator_stack.pop()
                    if op in {"sin", "cos", "tan", "log", "ln", "sqrt", "exp"}:
                        result = self.calculate(op, operand_stack.pop())
                    else:
                        result = self.calculate(op, operand_stack.pop(), operand_stack.pop())
                    if result == "Error":
                        return "Error"
                    operand_stack.push(result)
                operator_stack.push(token)

            elif token in {"(", "{"}:
                operator_stack.push(token)

            elif token in {")", "}"}:
                while not operator_stack.is_empty() and operator_stack.peek() not in {"(", "{"}:
                    op = operator_stack.pop()
                    if op in {"sin", "cos", "tan", "log", "ln", "sqrt", "exp"}:
                        result = self.calculate(op, operand_stack.pop())
                    else:
                        result = self.calculate(op, operand_stack.pop(), operand_stack.pop())
                    if result == "Error":
                        return "Error"
                    operand_stack.push(result)
                operator_stack.pop()

        while not operator_stack.is_empty():
            op = operator_stack.pop()
            if op in {"sin", "cos", "tan", "log", "ln", "sqrt", "exp"}:
                result = self.calculate(op, operand_stack.pop())
            else:
                result = self.calculate(op, operand_stack.pop(), operand_stack.pop())
            if result == "Error":
                return "Error"
            operand_stack.push(result)

        return operand_stack.pop()
