package re.imc.replaymenu.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DatabaseTable(tableName = "REPLAY_WAIT_FOR_PLAY")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReplayWaitForPlay {

    @DatabaseField(columnName = "UUID", id = true, width = 32)
    private String uuid;

    @DatabaseField(columnName = "REPLAY")
    private String replay;

}
