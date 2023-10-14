package re.imc.replaymenu.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@DatabaseTable(tableName = "REPLAY_WAIT_FOR_PLAY")
public class ReplayWaitForPlay {

    @DatabaseField(columnName = "UUID", id = true, width = 36)
    private String uuid;

    @DatabaseField(columnName = "REPLAY")
    private String replay;

}
