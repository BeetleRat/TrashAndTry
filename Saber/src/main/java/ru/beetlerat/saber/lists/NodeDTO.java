package main.java.ru.beetlerat.saber.lists;

public class NodeDTO {
    public int ID; // Добавлено для поддержания ссылочной целостности при строковой реализации
    public int PrevID;
    public int NextID;
    public int RandID; // произвольный элемент внутри списка
    public String Data;

    public NodeDTO(int ID, String data, int prevID, int nextID, int randID) {
        this.ID = ID;
        this.PrevID = prevID;
        this.NextID = nextID;
        this.RandID = randID;
        this.Data = data;
    }


    public String toString() {
        StringBuilder json = new StringBuilder();

        json.append("{\n");
        json.append("   \"ID\": ").append(ID).append("\n");
        json.append("   \"Data\": ").append(Data).append("\n");
        json.append("   \"PreviousID\": ").append(PrevID).append("\n");
        json.append("   \"NextID\": ").append(NextID).append("\n");
        json.append("   \"RandID\": ").append(RandID).append("\n");
        json.append("}\n");

        return json.toString();
    }
}
