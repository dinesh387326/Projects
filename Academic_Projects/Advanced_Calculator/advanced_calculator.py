from simple_calculator import SimpleCalculator
from stack import Stack

class AdvancedCalculator(SimpleCalculator):
    def calculate(self, operator, op1, op2):
        if operator == "+":
            return float(op1 + op2)
        elif operator == "-":
            return float(op2 - op1)
        elif operator == "*":
            return float(op2 * op1)
        elif operator == "/":
            if op1 == 0:
                return "Error"
            return float(op2 / op1)
        else:
            return "Error"

    def evaluate_expression(self, in_expression):
        list_tokens = self.tokenize(in_expression)
        if not self.check_brackets(list_tokens):
            result = "Error"
            self.history.insert(0,(in_expression,result))
            return result
        if not self.is_valid_expression(in_expression):
            result = "Error"
            self.history.insert(0, (in_expression, result))
            return result
        result = self.evaluate_list_tokens(list_tokens)
        self.history.insert(0,(in_expression,result))
        return result

    def is_valid_expression(self, input_expression):
        input_expression = input_expression.replace(" ", "")
        n = len(input_expression)
        num = []
        operators = []
        current_token = ""
        if input_expression[0] not in {'+', '-', '*', '/'} and input_expression[n- 1] not in {'+', '-', '*', '/'}:
            for char in input_expression:
                if char.isdigit():
                    current_token += char
                elif char in ("+", "-", "*", "/"):
                    if current_token:
                        num.append(int(current_token))
                        current_token = ""
                    operators.append(char)
            if current_token:
                num.append(int(current_token))
            if len(num)-1 == len(operators):
                return True
            else:
                return False
        else:
            return False

    def tokenize(self, input_expression):
        input_expression = input_expression.replace(" ", "")
        tokens = []
        current_token = ""

        for char in input_expression:
            if char.isdigit():
                current_token += char
            elif char in ("+", "-", "*", "/", "(", ")", "{", "}"):
                if current_token:
                    tokens.append(int(current_token))
                    current_token = ""
                tokens.append(char)

        if current_token:
            tokens.append(int(current_token))

        return tokens

    def check_brackets(self, list_tokens):
        stacks = Stack()
        for token in list_tokens:
            if token in ("(", "{"):
                previous_element = stacks.peek()
                stacks.push(token)
                if len(stacks)>=2:
                    if (token=='{') and ( previous_element == '('):
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
        precedence = {"+": 1, "-": 1, "*": 2, "/": 2}

        for token in list_tokens:
            if type(token) == int:
                operand_stack.push(token)
            elif token in ("+", "-", "*", "/"):
                while not operator_stack.is_empty() and (operator_stack.peek() != "(") and (operator_stack.peek() != "{") and (precedence[token] <= precedence[operator_stack.peek()]):
                    result = self.calculate(operator_stack.pop(), operand_stack.pop(), operand_stack.pop())
                    if result == "Error":
                        return "Error"
                    operand_stack.push(result)
                operator_stack.push(token)
            elif token in {"(","{"}:
                operator_stack.push(token)
            elif token in {"}",")"}:
                while not operator_stack.is_empty() and operator_stack.peek() != "(" and operator_stack.peek() != "{":
                    result = self.calculate(operator_stack.pop(), operand_stack.pop(), operand_stack.pop())
                    if result == "Error":
                        return "Error"
                    operand_stack.push(result)
                operator_stack.pop()

        while not operator_stack.is_empty():
            result = self.calculate(operator_stack.pop(), operand_stack.pop(), operand_stack.pop())
            if result == "Error":
                return "Error"
            operand_stack.push(result)

        return operand_stack.pop()
