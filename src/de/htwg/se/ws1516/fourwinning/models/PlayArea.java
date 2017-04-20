package de.htwg.se.ws1516.fourwinning.models;

import de.htwg.se.ws1516.fourwinning.models.Feld;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.HashMap;

public class PlayArea implements PlayAreaInterface
{
    private Feld[][] feld;
    int setX = 0;
    int setY = 0;
    private int columns;
    private int rows;

    //Konstruktor
    public PlayArea(int rows, int columns){
        feld = new Feld[rows][columns];
        this.columns = columns;
        this.rows = rows;
        buildArea(rows,columns);
    }

    //Spielfeld wird gebaut
    @Override
    public void buildArea(int rows, int columns){
        for(int i = 0; i < rows; i++){
            for (int j = 0; j<columns; j++){
                this.feld[i][j] = new Feld(i,j,null);
                this.feld[i][j].setSet(false);
            }
        }
    }

    //Chip an freie Stelle setzen
    @Override
    public int setChip(int column, Player p){
        if (column < 0
			|| column >= columns) return -2; //Zug fehlgeschlagen
        boolean find = false;
        int emptyRow = -1;
        int currentRow = 0;

        //Freien Platz an der Stelle der Spalte finden
        while (!find){
            if (feld[currentRow][column].getSet()){
                find = true;
                emptyRow = currentRow - 1;
            } else if (currentRow == rows-1){
                find = true;
                emptyRow = rows-1;
            }
            currentRow++;
        }

        //Chip wird gelegt
        if (emptyRow != -1){
            feld[emptyRow][column].setOwner(p);
            p.chipSetted();
            return emptyRow;            //Zug erfolgreich
        } 
        return -2;                          //Zug fehlgeschalgen
    }

    @Override
    public int getColumns(){
        return columns;
    }

    @Override
    public int getRows(){
        return rows;
    }

    @Override
    public Feld[][] getFeld(){
        return feld;
    }
    
    public void setFeld(Feld[][] zusatzfeld){
    	this.feld = zusatzfeld;
    }

    @Override
    public void clearFeld(){
        buildArea(this.rows, this.columns);
    }

    @Override
    public String toJson() {
        String result = "";
        try {
            int rows = this.rows;
            int columns = this.columns;
            Map[][] mapMatrix = new HashMap[rows][columns];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    mapMatrix[row][col] = new HashMap();
                    mapMatrix[row][col].put("feld", getIFeld(row, col));
                }
            }

            Map<String, Object> map = new HashMap();
            map.put("meta", this);
            map.put("grid", mapMatrix);
            ObjectMapper mapper = new ObjectMapper();

            result = mapper.writeValueAsString(map);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Feld getFeld(int row, int column) {
        return this.feld[row][column];
    }

    public FeldInterface getIFeld(int row, int column) { return getFeld(row, column); }
}