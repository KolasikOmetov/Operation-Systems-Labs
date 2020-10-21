package com.company;

public class Main {

    public static void main(String[] args) {

        final Stack stack = new Stack();
        final SystemCore systemCore = new SystemCore(stack);

        systemCore.showDescriptions();

        stack.push(3);
        stack.push(5);
        systemCore.call(0);

        stack.push(12.5);
        stack.push("str");
        systemCore.call(1);

        stack.push("str");
        stack.push("str");
        systemCore.call(2);

        stack.push(9.67);
        stack.push('o');
        systemCore.call(4);

        stack.push(0.3);
        stack.push(0.9);
        systemCore.call(3);

        stack.push("str");
        stack.push(12);
        systemCore.call(5);
    }
}
