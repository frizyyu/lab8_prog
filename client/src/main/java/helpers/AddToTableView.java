package helpers;

import supportive.MusicGenre;

public class AddToTableView {
    private static final long serialVersionUID = 111L;
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Long coordX; //Поле не может быть null
    private String coordY;
    private String creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int numberOfParticipants; //Значение поля должно быть больше 0
    private Integer albumsCount; //Поле не может быть null, Значение поля должно быть больше 0
    private MusicGenre genre; //Поле не может быть null
    private String studName;
    private String studAddr;

    public long getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCreationDate(){
        return creationDate;
    }
    public int getNumberOfParticipants(){
        return numberOfParticipants;
    }
    public Integer getAlbumsCount(){
        return albumsCount;
    }
    public MusicGenre getGenre(){
        return genre;
    }
    public String getStudName(){
        return studName;
    }
    public String getStudAddr(){
        return studAddr;
    }
    public Long getCoordX(){
        return coordX;
    }
    public String getCoordY(){
        return coordY;
    }

    public void setStudName(String studName){
        this.studName = studName;
    }
    public void setStudAddr(String studAddr){
        this.studAddr = studAddr;
    }
    public void setId(long id){
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCoordX(Long X){
        this.coordX = X;
    }
    public void setCoordY(String Y){
        this.coordY = Y;
    }
    public void setCreationDate(String creationDate){
        this.creationDate = creationDate;
    }
    public void setNumberOfParticipants(int numberOfParticipants){
        this.numberOfParticipants = numberOfParticipants;
    }
    public void setAlbumsCount(Integer albumsCount){
        this.albumsCount = albumsCount;
    }
    public void setGenre(MusicGenre genre){
        this.genre = genre;
    }
}
