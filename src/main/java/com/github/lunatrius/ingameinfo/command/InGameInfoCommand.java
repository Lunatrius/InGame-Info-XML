package com.github.lunatrius.ingameinfo.command;

import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.Ticker;
import com.github.lunatrius.ingameinfo.lib.Reference;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class InGameInfoCommand extends CommandBase {
	private InGameInfoCore core;

	public InGameInfoCommand(InGameInfoCore core) {
		this.core = core;
	}

	@Override
	public String getCommandName() {
		return "igi";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "commands.igi.usage";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "reload", "load", "save", "enable", "disable");
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("load")) {
				return getListOfStringsFromIterableMatchingLastWord(args, getFilenames());
			} else if (args[0].equalsIgnoreCase("save")) {
				return CommandBase.getListOfStringsMatchingLastWord(args, "InGameInfo.xml", "InGameInfo.json", "InGameInfo.txt");
			}
		}

		return null;
	}

	private List<String> getFilenames() {
		File[] files = this.core.getConfigDirectory().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("InGameInfo") && (name.endsWith(".xml") || name.endsWith(".json") || name.endsWith(".txt"));
			}
		});

		List<String> filenames = new ArrayList<String>();
		for (File file : files) {
			filenames.add(file.getName());
		}

		return filenames;
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				commandSender.addChatMessage(new ChatComponentTranslation("commands.igi.reload"));
				this.core.reloadConfig();
				return;
			} else if (args[0].equalsIgnoreCase("load")) {
				commandSender.addChatMessage(new ChatComponentTranslation("commands.igi.load", args[1]));
				if (this.core.loadConfig(args[1])) {
					Reference.config.setConfigName(args[1]);
					Reference.config.save();
				}
				return;
			} else if (args[0].equalsIgnoreCase("save")) {
				commandSender.addChatMessage(new ChatComponentTranslation("commands.igi.save", args[1]));
				this.core.saveConfig(args[1]);
				return;
			} else if (args[0].equalsIgnoreCase("enable")) {
				commandSender.addChatMessage(new ChatComponentTranslation("commands.igi.enable"));
				Ticker.enabled = true;
				return;
			} else if (args[0].equalsIgnoreCase("disable")) {
				commandSender.addChatMessage(new ChatComponentTranslation("commands.igi.disable"));
				Ticker.enabled = false;
				return;
			}
		}

		throw new WrongUsageException(getCommandUsage(commandSender));
	}

	@Override
	public int compareTo(Object obj) {
		return 0;
	}
}
