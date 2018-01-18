package br.com.marcioikeda.bakingapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by marcio.ikeda on 27/11/2017.
 */

@Entity(tableName = "ingredients",
        foreignKeys = @ForeignKey(
            entity = Recipe.class,
            parentColumns = "id",
            childColumns = "idFk",
            onDelete = ForeignKey.CASCADE)
        )
public class Ingredient implements Parcelable
{
    //This is not on json, but was created for Db.
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int idFk;
    @SerializedName("quantity")
    @Expose
    private double quantity;
    @SerializedName("measure")
    @Expose
    private String measure;
    @SerializedName("ingredient")
    @Expose
    private String ingredient;
    public final static Parcelable.Creator<Ingredient> CREATOR = new Creator<Ingredient>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return (new Ingredient[size]);
        }

    }
            ;

    protected Ingredient(Parcel in) {
        this.id = ((int) in.readValue(int.class.getClassLoader()));
        this.idFk = ((int) in.readValue(int.class.getClassLoader()));
        this.quantity = ((double) in.readValue((double.class.getClassLoader())));
        this.measure = ((String) in.readValue((String.class.getClassLoader())));
        this.ingredient = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Ingredient() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFk() {
        return idFk;
    }

    public void setIdFk(int idFk) {
        this.idFk = idFk;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(idFk);
        dest.writeValue(quantity);
        dest.writeValue(measure);
        dest.writeValue(ingredient);
    }

    public int describeContents() {
        return 0;
    }

}

