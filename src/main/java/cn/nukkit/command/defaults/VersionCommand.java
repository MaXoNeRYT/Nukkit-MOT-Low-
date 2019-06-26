package cn.nukkit.command.defaults;

import cn.nukkit.Nukkit;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.TextFormat;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class VersionCommand extends VanillaCommand {

    public VersionCommand(String name) {
        super(name,
                "%nukkit.command.version.description",
                "%nukkit.command.version.usage",
                new String[]{"ver", "about"}
        );
        this.setPermission("nukkit.command.version");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("\u00A76Version " + Nukkit.VERSION + ". \u00A7bThis server is running \u00A7cNukkit \u00A7aPetteriM1 Edition \u00A7bfor Minecraft Bedrock Edition \u00A76" + ProtocolInfo.MINECRAFT_VERSION_NETWORK + " \u00A7b(Protocol \u00A76" + ProtocolInfo.CURRENT_PROTOCOL + "\u00A7b) including experimental multiversion support.");

            if (sender.isOp()) {
                try {
                    URL url = new URL("https://api.github.com/repos/PetteriM1/NukkitPetteriM1Edition/commits/master");
                    URLConnection request = url.openConnection();
                    request.connect();
                    String latest = "git-" + new JsonParser().parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject().get("sha").getAsString().substring(0, 7);

                    if (!sender.getServer().getNukkitVersion().equals(latest) && !sender.getServer().getNukkitVersion().equals("git-null")) {
                        sender.sendMessage("\u00A7c[Update] \u00A7eThere is a new build of Nukkit PetteriM1 Edition available! Current: " + sender.getServer().getNukkitVersion() + " Latest: " + latest);
                    } else {
                        sender.sendMessage("\u00A7aYou are running the latest version.");
                    }
                } catch (Exception ignore) {}
            }
        } else {
            StringBuilder pluginName = new StringBuilder();
            for (String arg : args) pluginName.append(arg).append(" ");
            pluginName = new StringBuilder(pluginName.toString().trim());
            final boolean[] found = {false};
            final Plugin[] exactPlugin = {sender.getServer().getPluginManager().getPlugin(pluginName.toString())};

            if (exactPlugin[0] == null) {
                pluginName = new StringBuilder(pluginName.toString().toLowerCase());
                final String finalPluginName = pluginName.toString();
                sender.getServer().getPluginManager().getPlugins().forEach((s, p) -> {
                    if (s.toLowerCase().contains(finalPluginName)) {
                        exactPlugin[0] = p;
                        found[0] = true;
                    }
                });
            } else {
                found[0] = true;
            }

            if (found[0]) {
                PluginDescription desc = exactPlugin[0].getDescription();
                sender.sendMessage(TextFormat.DARK_GREEN + desc.getName() + TextFormat.WHITE + " version " + TextFormat.DARK_GREEN + desc.getVersion());
                if (desc.getDescription() != null) {
                    sender.sendMessage(desc.getDescription());
                }
                if (desc.getWebsite() != null) {
                    sender.sendMessage("Website: " + desc.getWebsite());
                }
                List<String> authors = desc.getAuthors();
                final String[] authorsString = {""};
                authors.forEach((s) -> authorsString[0] += s);
                if (authors.size() == 1) {
                    sender.sendMessage("Author: " + authorsString[0]);
                } else if (authors.size() >= 2) {
                    sender.sendMessage("Authors: " + authorsString[0]);
                }
            } else {
                sender.sendMessage(new TranslationContainer("nukkit.command.version.noSuchPlugin"));
            }
        }
        return true;
    }
}
