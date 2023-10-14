package re.imc.replaymenu.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "REPLAYINDEX")
public class ReplayIndex {
    @DatabaseField(columnName = "REPLAYID", id = true, width = 10)
    private String replayId;

    @DatabaseField(columnName = "REPLAYNAME")
    private String replayName;

    @DatabaseField(columnName = "REPLAYTYPE")
    private String replayType;

    @DatabaseField(columnName = "VERSION")
    private int version;

    @DatabaseField(columnName = "CHUNKLOC")
    private String chunkLoc;

    @DatabaseField(columnName = "TIME")
    private String time;

    @DatabaseField(columnName = "LENGTH")
    private int length;

    @DatabaseField(columnName = "IMPORTANTLEVEL")
    private int importantLevel;

    @DatabaseField(columnName = "LASTVIEW")
    private String lastView;

    @DatabaseField(columnName = "LASTVIEWEDBY")
    private String lastViewedBy;

    @DatabaseField(columnName = "VIEWS")
    private int views;

    @DatabaseField(columnName = "STORAGE")
    private String storage;
}
