package com.pizzaguy.ticket;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permission;

import com.pizzaguy.plugin.Plugin;

public class TicketCommand implements CommandExecutor {

    private Plugin plugin;
    private TicketSql sql;

    public TicketCommand(Plugin plugin, TicketSql sql) {
        this.plugin = plugin;
        this.sql = sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p;
        if (args.length > 0) {
            switch (args[0]) {
            case "create":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command is for players only.");
                    return true;
                }
                p = (Player) sender;
                if (!p.hasPermission("ticket.create")) {
                    p.sendMessage(ChatColor.RED + "You dont have permission to do this.");
                    return true;
                }
                if (args.length <= 1) {
                    p.sendMessage(ChatColor.RED + "Usage: /ticket create <message>");
                    return true;
                }
                if (sql.hasTicket(p.getUniqueId().toString())) {
                    p.sendMessage(ChatColor.RED + "You already have entered a ticket.");
                    return true;
                }
                if (args.length > 1) {
                    String ticket = "";
                    for (int i = 1; i < args.length; i++) {
                        ticket += args[i] += " ";
                    }
                    sql.createTicket(p.getUniqueId().toString(), ticket);
                    Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
                    for (Player player : players) {
                        if (player.hasPermission("ticket.select")) {
                            player.sendMessage(ChatColor.GOLD + "A new ticket has been entered.");
                        }
                    }
                    p.sendMessage(ChatColor.GOLD + "Your ticket has been entered.");
                }
                return true;
            case "delete":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command is for players only.");
                    return true;
                }
                p = (Player) sender;
                if (!p.hasPermission("ticket.delete")) {
                    p.sendMessage(ChatColor.RED + "You dont have permission to do this.");
                    return true;
                }
                if (!sql.hasTicket(p.getUniqueId().toString())) {
                    p.sendMessage(ChatColor.RED + "You have no ticket to delete.");
                    return true;
                }
                sql.deleteTicket(p.getUniqueId().toString());
                p.sendMessage(ChatColor.GOLD + "Your ticket has been deleted.");
                return true;
            case "list":
                if (sender.hasPermission(new Permission("ticket.list"))) {
                    sql.printTickets(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "You dont have permission to do this.");
                }
                return true;
            case "select":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command is for players only.");
                    return true;
                }
                p = (Player) sender;
                if (!p.hasPermission("ticket.select")) {
                    p.sendMessage(ChatColor.RED + "You dont have permission to do this.");
                    return true;
                }
                if (args.length != 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /ticket select <index>");
                    return true;
                }
                int index = 0;
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Usage: /ticket select <index>");
                    return true;
                }
                boolean contains = false;
                Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
                for (Player player : players) {
                    if (player.hasMetadata("Ticket")) {
                        if ((int) player.getMetadata("Ticket").get(0).asInt() == index) {
                            contains = true;
                            break;
                        }
                    }
                }
                if (!contains) {
                    String ticketowner = sql.getTicketOwner(index);
                    if (ticketowner == null) {
                        p.sendMessage(ChatColor.RED + "This ticket doesnt exist.");
                    } else if (ticketowner.equals(p.getUniqueId().toString())) {
                        if (p.hasPermission("ticket.select.self")) {
                            p.setMetadata("Ticket", new FixedMetadataValue(plugin, index));
                            p.sendMessage(ChatColor.GOLD + "Ticket selected.");
                        } else {
                            p.sendMessage(ChatColor.RED + "You cannot select your own ticket.");
                        }
                    } else {
                        p.setMetadata("Ticket", new FixedMetadataValue(plugin, index));
                        p.sendMessage(ChatColor.GOLD + "Ticket selected.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "This ticket is already selected.");
                    return true;
                }
                return true;
            case "done":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command is for players only.");
                    return true;
                }
                p = (Player) sender;
                if (!p.hasPermission("ticket.done")) {
                    p.sendMessage(ChatColor.RED + "You dont have permission to do this.");
                    return true;
                }
                if (p.hasMetadata("Ticket")) {
                    if (!sql.hasModerator(p.getUniqueId().toString()))
                        sql.addModerator(p.getUniqueId().toString());
                    sql.incModPoints(p.getUniqueId().toString(), 1);
                    sql.deleteTicket(p.getMetadata("Ticket").get(0).asInt());
                    p.removeMetadata("Ticket", plugin);
                    p.sendMessage(ChatColor.GOLD + "Ticket closed.");
                } else {
                    p.sendMessage(ChatColor.RED + "No ticket selected.");
                }
                return true;
            case "deselect":
                if (sender instanceof Player) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("This command is for players only.");
                        return true;
                    }
                    p = (Player) sender;
                    if (!p.hasPermission("ticket.deselect")) {
                        p.sendMessage(ChatColor.RED + "You dont have permission to do this.");
                        return true;
                    }
                    if (p.hasMetadata("Ticket")) {
                        p.removeMetadata("Ticket", plugin);
                        p.sendMessage(ChatColor.GOLD + "Ticket deselected.");
                    } else {
                        p.sendMessage(ChatColor.RED + "No ticket selected.");
                    }
                }
                return true;
            case "top":
                if (sender.hasPermission("ticket.top")) {
                    sql.printModerators(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "You dont have permission to do this.");
                }
                return true;
            case "?":
                printHelp(sender);
                return true;
            case "help":
                printHelp(sender);
                return true;
            default:
                sender.sendMessage(ChatColor.GOLD + "Do " + ChatColor.BLUE + "/ticket help " + ChatColor.GOLD
                        + "for a list of commands");
                return false;
            }
        }
        sender.sendMessage(
                ChatColor.GOLD + "Do " + ChatColor.BLUE + "/ticket help " + ChatColor.GOLD + "for a list of commands");
        return false;

    }

    public void printHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "List of commands:");
        if (sender.hasPermission("ticket.create")) {
            sender.sendMessage(ChatColor.RED + "/ticket create [message]" + ChatColor.GOLD + " to create a ticket");
        }
        if (sender.hasPermission("ticket.delete")) {
            sender.sendMessage(ChatColor.RED + "/ticket delete" + ChatColor.GOLD + " to delete your existing ticket");
        }
        if (sender.hasPermission("ticket.list")) {
            sender.sendMessage(ChatColor.RED + "/ticket list" + ChatColor.GOLD + " to get the list of current tickets");
        }
        if (sender.hasPermission("ticket.select")) {
            sender.sendMessage(
                    ChatColor.RED + "/ticket select <index>" + ChatColor.GOLD + " to select a ticket from the list");
        }
        if (sender.hasPermission("ticket.deselct")) {
            sender.sendMessage(
                    ChatColor.RED + "/ticket deselect" + ChatColor.GOLD + " to deselect the current selected ticket");
        }
        if (sender.hasPermission("ticket.done")) {
            sender.sendMessage(
                    ChatColor.RED + "/ticket done" + ChatColor.GOLD + " to mark the selected ticket as done");
        }
        if (sender.hasPermission("ticket.ranks")) {
            sender.sendMessage(ChatColor.RED + "/ticket top" + ChatColor.GOLD + " to see the top list");
        }
    }
}
