package com.enderaura.roles;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Enderqura on 08/08/2017 at 17:22.
 */
public enum Rank {

    OWNER("§6§lOwner §6", ChatColor.WHITE, 8),
    DEVELOPER("§4§lDev §4", ChatColor.WHITE, 7),
    ADMINISTRATOR("§c§lAdmin §c", ChatColor.WHITE, 6),
    MODERATOR("§a§lMod §a", ChatColor.WHITE, 5),
    EMPEROR("§1§lEmperor §1", ChatColor.GRAY, 4),
    DUKE("§9§lDuke §9", ChatColor.GRAY, 3),
    EARL("§3§lBaron §3", ChatColor.GRAY, 2),
    BARON("§b§lBaron §b", ChatColor.GRAY, 1),
    PLAYER("§8", ChatColor.DARK_GRAY, 0);

    String prefix;
    ChatColor chatColor;

    //Higherarchy incase getRankAtLeastMethod is needed
    int high;

    Rank(String prefix, ChatColor chatColor, int high){

        this.prefix = prefix;
        this.chatColor = chatColor;
        this.high = high;

    }

    public String getPrefix(){
        return prefix;
    }

    public int getHigh() {
        return high;
    }


    public ChatColor getChatColor() {
        return chatColor;
    }

}
