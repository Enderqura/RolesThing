package com.enderaura.roles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;

/**
 * Created by Enderqura on 08/08/2017 at 17:19.
 */
public class Roles extends JavaPlugin implements Listener{

    private Connection connection;
    private String table;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("setrank").setExecutor(new Commands(this));
        getCommand("listranks").setExecutor(new Commands(this));

        setupSql();

    }

    @Override
    public void onDisable() {
        setConnection(null);
    }

    private void setupSql(){

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String database = getConfig().getString("database.name");
        String username = getConfig().getString("database.user");
        String password = getConfig().getString("database.password");
        table = getConfig().getString("database.table");


        try {

            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                setConnection(
                        DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database,
                                username, password));

                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    boolean rankAtLeast(Player player, Rank rank){

        if(getRank(player).getHigh() >= rank.getHigh()){

            return true;

        }

        return false;
    }

    private Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }


    void setRank(Rank rank, UUID player){
        try {

            Player p = Bukkit.getPlayer(player);
            Rank r = getRank(p);


            PreparedStatement statement = this.getConnection()
                    .prepareStatement("UPDATE " + this.table + " SET RANK=? WHERE UUID=?");
            statement.setString(1, rank.toString());
            statement.setString(2, player.toString());
            statement.executeUpdate();

            if(p != null){

                p.setPlayerListName(getRank(p).getPrefix() + p.getName());

            }


        }
        catch(SQLException e){

            e.printStackTrace();

        }
    }

    private Rank getRank(Player player){

        UUID uuid = player.getUniqueId();
        Rank rank;

        try {
            PreparedStatement statement = this.getConnection()
                    .prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();

            rank = Rank.valueOf(results.getString("Rank"));
        }
        catch(SQLException e){

            e.printStackTrace();
            return null;

        }



        return rank;
    }

    private String getPlayerName(UUID uuid){

        String toReturn;

        try {
            PreparedStatement statement = this.getConnection()
                    .prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();

            toReturn = results.getString("Name");
        }
        catch(SQLException e){

            e.printStackTrace();
            return null;

        }



        return toReturn;

    }

    private void setPlayerName(UUID uuid, String name){

        try {

            PreparedStatement statement = this.getConnection()
                    .prepareStatement("UPDATE " + this.table + " SET NAME=? WHERE UUID=?");
            statement.setString(1, name);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        }
        catch(SQLException e){

            e.printStackTrace();

        }

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){

        String prefix;
        ChatColor color;

        try {

            prefix = getRank(e.getPlayer()).getPrefix();
            color = getRank(e.getPlayer()).getChatColor();

        }

        catch(NullPointerException ex){

            getLogger().severe(e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId().toString() + ")'s rank is invalid.");
            e.getPlayer().sendMessage("Your rank is invalid, contact a member of staff!");
            e.setCancelled(true);
            return;

        }

        e.setFormat(prefix + "%s " + ChatColor.BLUE + ">> " + color + "%s");

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){

        Player player = e.getPlayer();

        if(!(playerExists(player.getUniqueId()))){



            try {
                PreparedStatement insert = this.getConnection()
                        .prepareStatement("INSERT INTO " + this.table + " (UUID,RANK) VALUES (?, ?)");
                insert.setString(1, player.getUniqueId().toString());
                insert.setString(2, "Player");
                insert.executeUpdate();
            }
            catch(SQLException exception){

                exception.printStackTrace();

            }

        }
        try {
            e.getPlayer().setPlayerListName(getRank(e.getPlayer()).getPrefix() + e.getPlayer().getName());
        }
        catch(NullPointerException ex){

            getLogger().severe(e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId().toString() + ")'s rank is invalid.");
            e.getPlayer().sendMessage("Your rank is invalid, contact a member of staff!");

        }

        if(!e.getPlayer().getName().equals(getPlayerName(e.getPlayer().getUniqueId()))) setPlayerName(e.getPlayer().getUniqueId(), e.getPlayer().getName());

    }

    private boolean playerExists(UUID uuid) {
        try {
            PreparedStatement statement = this.getConnection()
                    .prepareStatement("SELECT * FROM " + this.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
