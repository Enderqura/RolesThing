package com.enderaura.roles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Enderqura on 08/08/2017 at 17:46.
 */
public class Commands implements CommandExecutor {

    private static Roles ranks;

    Commands(Roles ranks){

        Commands.ranks = ranks;

    }



    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(command.getName().equalsIgnoreCase("setrank")){

            if(args.length == 2){
                if(sender instanceof Player) {
                    Player player = (Player) sender;

                    if (ranks.rankAtLeast(player, Rank.ADMINISTRATOR)) {

                        UUID target;
                        String online;

                        if (Bukkit.getPlayer(args[0]) == null) {

                            online = "(Offline Player)";
                            target = Bukkit.getOfflinePlayer(args[0]).getUniqueId();

                        } else {

                            online = Bukkit.getPlayer(args[0]).getName();
                            target = Bukkit.getPlayer(args[0]).getUniqueId();

                        }

                        if (target != null) {

                            ranks.setRank(Rank.valueOf(args[1]), target);

                            player.sendMessage("§6§lSet " + online + "'s rank to " + args[1] + "!");

                            return true;


                        } else {

                            sender.sendMessage("§4§lError: §cPlayer not found!");
                            return true;

                        }


                    } else {

                        sender.sendMessage("§4§lError: §cYou must be at least Administrator to use this command!");
                        return true;

                    }
                }else{

                    UUID target;

                    if (Bukkit.getPlayer(args[0]) == null) {

                        target = Bukkit.getOfflinePlayer(args[0]).getUniqueId();

                    } else {

                        target = Bukkit.getPlayer(args[0]).getUniqueId();

                    }

                    if (target != null) {

                        ranks.setRank(Rank.valueOf(args[1]), target);
                        return true;


                    } else {

                        sender.sendMessage("§4§lError: §cPlayer not found!");
                        return true;

                    }

                    }


                }

            }
            else{

                sender.sendMessage("§4§lError: §cUse command as /setrank [player] [rank]!");
                return true;


            }

        if(command.getName().equalsIgnoreCase("listranks")){

            if(sender instanceof Player){

                if(ranks.rankAtLeast((Player) sender, Rank.MODERATOR)){

                    listRanks(sender);

                }else{


                    sender.sendMessage("§4§lError: §cYou must be at least Junior Moderator to use this command!");
                    return true;

                }

            }else{

                listRanks(sender);

            }

            return true;
        }

        return false;
    }

    private void listRanks(CommandSender sender){

        sender.sendMessage(ChatColor.GREEN + "List of all ranks");
        sender.sendMessage("\n\n");

        for(Rank rank : Rank.values()){

            sender.sendMessage(ChatColor.RED + " [" + ChatColor.YELLOW + rank.toString() + ChatColor.RED + "]");

        }


    }


}
