package main.java.ru.beetlerat.saber.lists;

import java.io.Serializable;

public class ListNode implements Serializable {
    public ListNode Prev;
    public ListNode Next;
    public ListNode Rand; // произвольный элемент внутри списка
    public String Data;
}
