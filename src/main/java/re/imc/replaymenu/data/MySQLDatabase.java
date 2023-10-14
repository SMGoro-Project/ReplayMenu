package re.imc.replaymenu.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import re.imc.replaymenu.ReplayMenu;
import re.imc.replaymenu.data.model.ReplayHistory;
import re.imc.replaymenu.data.model.ReplayIndex;
import re.imc.replaymenu.data.model.ReplayWaitForPlay;

import java.sql.SQLException;

public class MySQLDatabase {

    @Getter
    private static Dao<ReplayHistory, String> replayHistoryDao;
    @Getter
    private static Dao<ReplayIndex, String> replayIndexDao;
    @Getter
    private static Dao<ReplayWaitForPlay, String> replayWaitForPlayDao;


    public static void load(ConfigurationSection config) throws SQLException {
        String hostname = config.getString("hostname");
        String user = config.getString("user");
        String password = config.getString("password");
        String database = config.getString("database");
        String connectionParameters = config.getString("connection-parameters");

        String url = "jdbc:mysql://" + hostname + "/" + database + connectionParameters;
        JdbcPooledConnectionSource connection = new JdbcPooledConnectionSource(url, user, password);
        TableUtils.createTableIfNotExists(connection, ReplayWaitForPlay.class);

        replayHistoryDao = DaoManager.createDao(connection, ReplayHistory.class);
        replayIndexDao = DaoManager.createDao(connection, ReplayIndex.class);
        replayWaitForPlayDao = DaoManager.createDao(connection, ReplayWaitForPlay.class);

    }


}
