package com.bruce.byjson;

/**
 * Created by bruceyuan on 17-6-17.
 */
public class Stack<T> {
    static class Node<T> {
        private T value;
        private Node<T> next;

        public Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> header;
    private int size;

    public T push(T value) {
        Node<T> item = new Node<>(value, header);
        header = item;
        size++;
        return value;
    }

    public T pop() {
        if (size == 0){
            throw new RuntimeException("stack is empty.");
        }
        T value = header.value;
        header = header.next;
        size--;
        return value;
    }

    public T peek() {
        if (size == 0){
            throw new RuntimeException("stack is empty.");
        }
        return header.value;
    }

    public boolean empty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}

