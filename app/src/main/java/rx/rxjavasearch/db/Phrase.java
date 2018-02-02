package rx.rxjavasearch.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.rxjavasearch.model.DuckGoSuggestion;

@Table(database = MyDatabase.class)
public class Phrase extends BaseModel {

    @Column
    @PrimaryKey
    int id;

    @Column
    public String name;

    @Column
    private Date timestamp;

    @Column
    private String sentencies;
    //List<DuckGoSuggestion> listValues;

    public void setName(String name) {
        this.name = name;
    }

    public void setSentencies(String sentencies) {
        this.sentencies = sentencies;
    }

    public String getSentencies() {
        return sentencies;
    }

    /*
        public void setList(List<DuckGoSuggestion> list) {
            this.listValues = list;
        }
    */
    public void setDateFromString(String date) {
        try {
            SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
            sf.setLenient(true);
            this.timestamp = sf.parse(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
