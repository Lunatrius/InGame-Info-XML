package com.github.lunatrius.ingameinfo.command;

import com.github.lunatrius.ingameinfo.Config;
import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.InGameInfoXML;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatMessageComponent;

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
				commandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.igi.reload"));
				this.core.reloadConfig();
				return;
			} else if (args[0].equalsIgnoreCase("load")) {
				commandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.igi.load", args[1]));
				if (this.core.loadConfig(args[1])) {
					Config config = InGameInfoXML.instance.config;
					config.setConfigName(args[1]);
					config.save();
				}
				return;
			} else if (args[0].equalsIgnoreCase("save")) {
				commandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("commands.igi.save", args[1]));
				this.core.saveConfig(args[1]);
				return;
			} else if (args[0].equalsIgnoreCase("enable")) {
				commandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.igi.enable"));
				InGameInfoXML.instance.enabled = true;
				return;
			} else if (args[0].equalsIgnoreCase("disable")) {
				commandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.igi.disable"));
				InGameInfoXML.instance.enabled = false;
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
